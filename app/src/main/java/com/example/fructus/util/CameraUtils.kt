package com.example.fructus.util

import android.content.Context
import android.graphics.*
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import org.opencv.core.Point
import kotlin.math.min
import kotlin.math.max
// 🔹 OpenCV imports
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

data class ClassificationResult(
    val label: String,
    val confidence: Float
)

// --------------------- FRUIT TYPE CLASSIFIER ---------------------
fun classifyFruit(bitmap: Bitmap, context: Context): ClassificationResult {
    val modelName = "fruit_type_model.tflite"
    val labels = listOf(
        "Cavendish", "Lakatan", "Carabao", "Saba",
        "Spoiled Banana", "Spoiled Mango", "Spoiled Tomato", "Tomato"
    )

    val model = Interpreter(loadModelFile(context, modelName))
    val cropped = bitmap.cropToScanBox(context)

    if (cropped.isMostlyBackground()) {
        model.close()
        return ClassificationResult("No fruit detected", 0f)
    }

    val input = preprocessBitmap(cropped)
    val output = Array(1) { FloatArray(labels.size) }
    model.run(input, output)
    model.close()

    var maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    var confidence = if (maxIndex != -1) output[0][maxIndex] else 0f
    var predictedLabel = if (maxIndex != -1) labels[maxIndex] else "Unknown"

    val thresholds = mapOf(
        "Tomato" to 0.9f,
        "Lakatan" to 0.85f,
        "Saba" to 0.85f,
        "Cavendish" to 0.85f,
        "Carabao" to 0.95f,
        "Spoiled Banana" to 0.9f,
        "Spoiled Mango" to 0.7f,
        "Spoiled Tomato" to 0.8f
    )
    val threshold = thresholds[predictedLabel] ?: 0.6f

    // 🟢 --- Cavendish vs. Lakatan Heuristic Enhancement ---
    if (predictedLabel == "Cavendish" || predictedLabel == "Lakatan") {
        val curvature = cropped.computeCurvatureFactor()
        val hue = cropped.computeHueBalance()
        Log.d("FRUCTUS_LOG", "Curvature: $curvature, Hue: $hue")

        // Cavendish: More curved, lighter hue (yellow)
        // Lakatan: Straighter, more orange hue
        when {
            curvature > 0.3f && hue > 22f -> {
                predictedLabel = "Cavendish"
                confidence = (confidence + 0.1f).coerceAtMost(1f)
            }
            curvature < 0.25f && hue < 22f -> {
                predictedLabel = "Lakatan"
                confidence = (confidence + 0.1f).coerceAtMost(1f)
            }
        }
    }

    return if (confidence >= threshold) {
        ClassificationResult(predictedLabel, confidence)
    } else {
        ClassificationResult("No fruit detected", confidence)
    }
}

// --------------------- CURVATURE FACTOR ---------------------
fun Bitmap.computeCurvatureFactor(): Float {
    val src = Mat()
    Utils.bitmapToMat(this, src)
    Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY)
    Imgproc.GaussianBlur(src, src, Size(7.0, 7.0), 0.0)

    // Edge detection
    val edges = Mat()
    Imgproc.Canny(src, edges, 40.0, 120.0)

    // Find contours
    val contours = mutableListOf<MatOfPoint>()
    Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
    if (contours.isEmpty()) {
        src.release(); edges.release()
        return 0f
    }

    // Largest contour = fruit
    val largest = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return 0f

    // Smooth the contour → reduces sensitivity
    val approx = MatOfPoint2f()
    val contour2f = MatOfPoint2f(*largest.toArray())
    Imgproc.approxPolyDP(contour2f, approx, 0.01 * Imgproc.arcLength(contour2f, true), true)

    // Fit ellipse → gives major/minor axis → curvature measure
    if (approx.rows() < 5) {
        src.release(); edges.release()
        return 0f
    }

    val ellipse = Imgproc.fitEllipse(approx)

    val major = max(ellipse.size.width, ellipse.size.height)
    val minor = min(ellipse.size.width, ellipse.size.height)

    // Curvature ratio (0 = straight, higher = more curve)
    val curvature = 1f - (minor / major).toFloat()

    src.release(); edges.release()
    return curvature.coerceIn(0f, 1f)
}


// --------------------- HUE BALANCE (Improved Median-Based) ---------------------
fun Bitmap.computeHueBalance(): Float {
    val mat = Mat()
    Utils.bitmapToMat(this, mat)
    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2HSV)

    val hsvChannels = mutableListOf<Mat>()
    Core.split(mat, hsvChannels)
    val h = hsvChannels[0]
    val s = hsvChannels[1]
    val v = hsvChannels[2]

    val mask = Mat()
    Core.inRange(v, Scalar(50.0), Scalar(230.0), mask)

    val hueValues = Mat()
    h.copyTo(hueValues, mask)
    val hueList = mutableListOf<Double>()

    for (row in 0 until hueValues.rows()) {
        for (col in 0 until hueValues.cols()) {
            val hueVal = hueValues.get(row, col)?.firstOrNull()?.toDouble() ?: continue
            if (hueVal > 0) hueList.add(hueVal)
        }
    }

    mat.release(); h.release(); s.release(); v.release(); mask.release(); hueValues.release()
    if (hueList.isEmpty()) return 0f

    hueList.sort()
    val medianHue = hueList[hueList.size / 2]
    return medianHue.toFloat()
}

// --------------------- RIPENESS CLASSIFICATION ---------------------
fun classifyRipeness(fruitType: String, bitmap: Bitmap, context: Context): ClassificationResult {
    if (
        fruitType.equals("Spoiled Banana", true) ||
        fruitType.equals("Spoiled Tomato", true) ||
        fruitType.equals("Spoiled Mango", true)
    ) {
        return ClassificationResult("Spoiled", 1f)
    }

    val modelName = when (fruitType.lowercase()) {
        "cavendish" -> "banana_cavendish_model.tflite"
        "lakatan" -> "banana_lakatan_model.tflite"
        "saba" -> "banana_saba_model.tflite"
        "carabao" -> "mango_model.tflite"
        "tomato" -> "tomato_model.tflite"
        else -> return ClassificationResult("Unknown", 0f)
    }

    val labels = listOf("Overripe", "Ripe", "Unripe")
    val model = Interpreter(loadModelFile(context, modelName))
    val segmented = bitmap.segmentFruit()
    val cropped = segmented.cropToScanBox(context)


    val input = preprocessBitmap(cropped)
    val output = Array(1) { FloatArray(labels.size) }
    model.run(input, output)
    model.close()

    var maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    var confidence = if (maxIndex != -1) output[0][maxIndex] else 0f
    var predictedLabel = if (maxIndex != -1) labels[maxIndex] else "Unknown"


    if (!fruitType.equals("tomato", ignoreCase = true)) {
        val colorAssist = cropped.computeColorAssist()
        Log.d("FRUCTUS_LOG", "Color Assist Ratio: $colorAssist")

        when {
            colorAssist > 0.6f && predictedLabel == "Unripe" -> {
                predictedLabel = "Ripe"
                confidence = (confidence + 0.15f).coerceAtMost(1f)
            }
            colorAssist < 0.3f && predictedLabel == "Ripe" -> {
                predictedLabel = "Unripe"
                confidence = (confidence + 0.15f).coerceAtMost(1f)
            }
        }
    }

    // Exempt ONLY Unripe Saba from spot logic
    val skipSpotLogic = fruitType.equals("saba", ignoreCase = true) &&
            predictedLabel.equals("Unripe", ignoreCase = true)

    if (!fruitType.equals("tomato", ignoreCase = true) && !skipSpotLogic) {

        val spotFactor = cropped.computeSpotFactor()
        Log.d("FRUCTUS_LOG", "Spot Factor (ripeness): $spotFactor")

        val thresholds = when (fruitType.lowercase()) {
            "cavendish" -> Pair(60f, 50f)
            "lakatan" -> Pair(50f, 40f)
            "saba" -> Pair(50f, 40f)
            "carabao" -> Pair(50f, 40f)
            else -> Pair(42f, 30f)
        }

        val spoiledThreshold = thresholds.first
        val overripeThreshold = thresholds.second

        when {
            spotFactor >= spoiledThreshold &&
                    (predictedLabel == "Ripe" || predictedLabel == "Overripe") -> {
                predictedLabel = "Spoiled"
                confidence = 0.95f
            }

            spotFactor in overripeThreshold..(spoiledThreshold - 1) &&
                    predictedLabel == "Ripe" -> {
                predictedLabel = "Overripe"
                confidence = (confidence + 0.1f).coerceAtMost(1f)
            }
        }
    } else {
        Log.d("FRUCTUS_LOG", "Spot factor skipped for this case.")
    }


    val ripenessThreshold = 0.6f
    return if (confidence >= ripenessThreshold) {
        ClassificationResult(predictedLabel, confidence)
    } else {
        ClassificationResult("Unknown", confidence)
    }
}



// --------------------- MANGO RIPENING METHOD CLASSIFIER ---------------------
fun classifyRipeningMethod(ripenessLabel: String, bitmap: Bitmap, context: Context): ClassificationResult {
    val modelName = when (ripenessLabel.lowercase()) {
        "ripe" -> "mango_ripe_model.tflite"
        "overripe" -> "mango_overripe_model.tflite"
        else -> return ClassificationResult("Unknown", 0f)
    }

    val model = Interpreter(loadModelFile(context, modelName))
    val labels = listOf("Artificial", "Natural")

    val segmented = bitmap.segmentFruit()
    val cropped = segmented.cropToScanBox(context)

    if (cropped.isMostlyBackground()) {
        model.close()
        return ClassificationResult("No fruit detected", 0f)
    }

    val input = preprocessBitmap(cropped)
    val output = Array(1) { FloatArray(labels.size) }
    model.run(input, output)
    model.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    var confidence = if (maxIndex != -1) output[0][maxIndex] else 0f
    var predictedLabel = if (maxIndex != -1) labels[maxIndex] else "Unknown"

    // 🟢 Spot factor based on research paper
    val spotFactor = cropped.computeSpotFactor()
    Log.d("FRUCTUS_LOG", "Spot Factor: $spotFactor")

    predictedLabel = if (spotFactor >= 7f) "Natural" else "Artificial"
    confidence = (spotFactor / 10f).coerceIn(0f, 1f)

    return ClassificationResult(predictedLabel, confidence)
}

// --------------------- RIPENING STAGE MAPPER ---------------------
fun mapRipeningStage(fruitResult: ClassificationResult): String {
    return when {
        fruitResult.label.contains("Spoiled", ignoreCase = true) -> "Spoiled"
        fruitResult.label.contains("Unripe", ignoreCase = true) -> "Unripe"
        fruitResult.label.contains("Ripe", ignoreCase = true) -> "Ripe"
        fruitResult.label.contains("Overripe", ignoreCase = true) -> "Overripe"
        else -> "Unknown"
    }
}

// --------------------- HELPERS ---------------------
fun loadModelFile(context: Context, modelFileName: String): ByteBuffer {
    val fileDescriptor = context.assets.openFd(modelFileName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
    val inputSize = 224
    val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
    val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
    byteBuffer.order(ByteOrder.nativeOrder())
    val pixels = IntArray(inputSize * inputSize)
    resized.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
    for (pixel in pixels) {
        val r = ((pixel shr 16) and 0xFF) / 255.0f
        val g = ((pixel shr 8) and 0xFF) / 255.0f
        val b = (pixel and 0xFF) / 255.0f
        byteBuffer.putFloat(r)
        byteBuffer.putFloat(g)
        byteBuffer.putFloat(b)
    }
    return byteBuffer
}

@androidx.camera.core.ExperimentalGetImage
fun ImageProxy.toBitmap(): Bitmap? {
    val image = this.image ?: return null
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val jpegBytes = out.toByteArray()
    this.close()
    return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
}

fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String = "fruit_${System.currentTimeMillis()}.jpg"): String {
    val dir = File(context.filesDir, "fruits")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, fileName)
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
    return file.absolutePath
}

// --------------------- FIXED CROP TO SCAN BOX ---------------------
fun Bitmap.cropToScanBox(context: Context): Bitmap {
    val screenWidthPx = context.resources.displayMetrics.widthPixels
    val screenHeightPx = context.resources.displayMetrics.heightPixels
    val boxSizePx = (460 * context.resources.displayMetrics.density).toInt()

    val scaleX = this.width.toFloat() / screenWidthPx
    val scaleY = this.height.toFloat() / screenHeightPx
    val boxWidthOnBitmap = (boxSizePx * scaleX).toInt()
    val boxHeightOnBitmap = (boxSizePx * scaleY).toInt()
    val left = (this.width - boxWidthOnBitmap) / 2
    val top = (this.height - boxHeightOnBitmap) / 2

    val safeLeft = left.coerceAtLeast(0)
    val safeTop = top.coerceAtLeast(0)
    val safeWidth = boxWidthOnBitmap.coerceAtMost(this.width - safeLeft)
    val safeHeight = boxHeightOnBitmap.coerceAtMost(this.height - safeTop)

    return Bitmap.createBitmap(this, safeLeft, safeTop, safeWidth, safeHeight)
}

// --------------------- BACKGROUND CHECK ---------------------
fun Bitmap.isMostlyBackground(
    blackThreshold: Int = 30,
    whiteThreshold: Int = 220,
    grayTolerance: Int = 15,
    ratio: Double = 0.9
): Boolean {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    var backgroundCount = 0

    for (p in pixels) {
        val r = (p shr 16) and 0xFF
        val g = (p shr 8) and 0xFF
        val b = p and 0xFF
        val avg = (r + g + b) / 3

        if (avg < blackThreshold) {
            backgroundCount++
        } else if (avg > whiteThreshold) {
            backgroundCount++
        } else if (
            kotlin.math.abs(r - g) < grayTolerance &&
            kotlin.math.abs(g - b) < grayTolerance &&
            kotlin.math.abs(r - b) < grayTolerance
        ) {
            backgroundCount++
        }
    }

    return backgroundCount > width * height * ratio
}

// --------------------- SPOT FACTOR DETECTION (Research Accurate) ---------------------
fun Bitmap.computeSpotFactor(): Float {
    val src = Mat()
    Utils.bitmapToMat(this, src)

    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
    Imgproc.medianBlur(gray, gray, 5)

    val edges = Mat()
    Imgproc.Canny(gray, edges, 10.0, 100.0)
    Imgproc.dilate(edges, edges, Mat(), Point(-1.0, -1.0), 1)

    val contours = mutableListOf<MatOfPoint>()
    Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
    if (contours.isEmpty()) {
        src.release(); gray.release(); edges.release()
        return 0f
    }

    val largest = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return 0f
    val mask = Mat.zeros(src.size(), CvType.CV_8UC1)
    Imgproc.drawContours(mask, listOf(largest), -1, Scalar(255.0), -1)

    val thresh = Mat()
    Imgproc.adaptiveThreshold(
        gray, thresh, 255.0,
        Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
        Imgproc.THRESH_BINARY_INV, 11, 2.0
    )

    val spotPixelsMat = Mat()
    Core.bitwise_and(thresh, mask, spotPixelsMat)
    val totalBlackPixels = Core.countNonZero(spotPixelsMat)
    val mangoArea = Core.countNonZero(mask)
    val adjustedSpots = totalBlackPixels * 0.955f
    val spotFactor = if (mangoArea > 0) (adjustedSpots / mangoArea) * 100f else 0f

    src.release(); gray.release(); edges.release(); mask.release()
    thresh.release(); spotPixelsMat.release()

    return spotFactor
}

// --------------------- 11-STEP SEGMENTATION ---------------------
fun Bitmap.segmentFruit(): Bitmap {
    if (this.isMostlyBackground()) {
        return this
    }

    val src = Mat()
    Utils.bitmapToMat(this, src)

    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
    Imgproc.medianBlur(gray, gray, 5)

    val edges = Mat()
    Imgproc.Canny(gray, edges, 10.0, 100.0)
    Imgproc.dilate(edges, edges, Mat(), Point(-1.0, -1.0), 1)

    val contours = mutableListOf<MatOfPoint>()
    Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

    if (contours.isEmpty()) return this
    val largest = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return this

    val mask = Mat.zeros(src.size(), CvType.CV_8UC1)
    Imgproc.drawContours(mask, listOf(largest), -1, Scalar(255.0), -1)

    val result = Mat()
    src.copyTo(result, mask)
    val rect = Imgproc.boundingRect(largest)
    val cropped = Mat(result, rect)
    val output = Bitmap.createBitmap(cropped.width(), cropped.height(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(cropped, output)

    if (output.width < width * 0.2 || output.height < height * 0.2) {
        return this
    }

    return output
}

// --------------------- GALLERY IMAGE DETECTION ---------------------
suspend fun analyzeBitmap(
    bitmap: Bitmap,
    context: Context
): Triple<ClassificationResult, ClassificationResult?, ClassificationResult?> {

    // 🔹 Preprocess image just like camera
    val processedBitmap = bitmap.prepareForModel()  // optional helper if you use normalization, rotation, etc.

    // 🔹 Step 1: Fruit classification
    val fruitResult = classifyFruit(processedBitmap, context)
    var ripenessResult: ClassificationResult? = null
    var ripeningMethodResult: ClassificationResult? = null

    // 🔹 Step 2: Continue only if fruit is detected
    if (fruitResult.label != "No fruit detected") {

        // --- Cavendish or Carabao banana or mango get their own rules ---
        ripenessResult = classifyRipeness(fruitResult.label, processedBitmap, context)

        // --- Spot factor, color assist, etc. already happen inside classifyRipeness() ---
        // So you don't need to manually call those here.

        // 🔹 Step 3: If it's a Carabao mango and is ripe/overripe → check ripening method
        if (fruitResult.label.equals("Carabao", true) &&
            (ripenessResult.label.equals("Ripe", true) || ripenessResult.label.equals("Overripe", true))
        ) {
            val segmented = processedBitmap.segmentFruit()
            ripeningMethodResult = classifyRipeningMethod(ripenessResult.label, segmented, context)
        }
    }

    // 🔹 Logging
    Log.d("FRUCTUS_LOG", "📸 [GALLERY DETECTION]")
    Log.d("FRUCTUS_LOG", "Fruit Type: ${fruitResult.label} (Conf: ${fruitResult.confidence})")
    ripenessResult?.let {
        Log.d("FRUCTUS_LOG", "Ripeness: ${it.label} (Conf: ${it.confidence})")
    }
    ripeningMethodResult?.let {
        Log.d("FRUCTUS_LOG", "Ripening Method: ${it.label} (Conf: ${it.confidence})")
    }

    return Triple(fruitResult, ripenessResult, ripeningMethodResult)
}

fun Bitmap.computeColorAssist(): Float {
    val small = Bitmap.createScaledBitmap(this, 64, 64, true)
    val pixels = IntArray(small.width * small.height)
    small.getPixels(pixels, 0, small.width, 0, 0, small.width, small.height)

    var yellowish = 0
    var greenish = 0

    for (p in pixels) {
        val r = (p shr 16) and 0xFF
        val g = (p shr 8) and 0xFF
        val b = p and 0xFF

        // Hue-like logic but faster (no color conversion)
        if (r > 150 && g > 100 && b < 100) yellowish++
        else if (g > r + 15 && g > b + 15) greenish++   // much less sensitive
    }

    small.recycle()
    val total = yellowish + greenish
    return if (total == 0) 0f else yellowish.toFloat() / total
}
fun Bitmap.prepareForModel(): Bitmap {
    // Ensure same input size and orientation as camera
    val targetSize = 224
    val scaled = Bitmap.createScaledBitmap(this, targetSize, targetSize, true)
    return scaled
}
