package com.example.fructus.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

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

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val confidence = if (maxIndex != -1) output[0][maxIndex] else 0f
    val predictedLabel = if (maxIndex != -1) labels[maxIndex] else "Unknown"

    // 🔹 Fruit-specific thresholds
    val thresholds = mapOf(
        "Tomato" to 0.9f,
        "Lakatan" to 0.9f,
        "Saba" to 0.8f,
        "Cavendish" to 0.8f,
        "Carabao" to 0.6f,
        "Spoiled Banana" to 0.55f,
        "Spoiled Mango" to 0.6f,
        "Spoiled Tomato" to 0.6f
    )

    val threshold = thresholds[predictedLabel] ?: 0.8f

    return if (confidence >= threshold) {
        ClassificationResult(predictedLabel, confidence)
    } else {
        ClassificationResult("No fruit detected", confidence)
    }
}

// --------------------- RIPENESS CLASSIFIER ---------------------
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

    val cropped = bitmap.cropToScanBox(context)

    if (cropped.isMostlyBackground()) {
        model.close()
        return ClassificationResult("No fruit detected", 0f)
    }

    val input = preprocessBitmap(cropped)
    val output = Array(1) { FloatArray(labels.size) }
    model.run(input, output)
    model.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val confidence = if (maxIndex != -1) output[0][maxIndex] else 0f
    val predictedLabel = if (maxIndex != -1) labels[maxIndex] else "Unknown"

    val ripenessThreshold = 0.45f

    return if (confidence >= ripenessThreshold) {
        ClassificationResult(predictedLabel, confidence)
    } else {
        ClassificationResult("Unknown", confidence)
    }
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

    // Your overlay is 460.dp square
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

// --------------------- HISTOGRAM UTILS ---------------------
fun calculateHistogram(bitmap: Bitmap): IntArray {
    val histogram = IntArray(256)
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    for (pixel in pixels) {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        val gray = (r + g + b) / 3
        histogram[gray]++
    }
    return histogram
}

fun compareHistograms(h1: IntArray?, h2: IntArray?): Float {
    if (h1 == null || h2 == null) return 0f
    val minLen = minOf(h1.size, h2.size)
    var diff = 0L
    var total = 0L
    for (i in 0 until minLen) {
        diff += kotlin.math.abs(h1[i] - h2[i])
        total += h1[i] + h2[i]
    }
    return if (total == 0L) 0f else 1f - (diff.toFloat() / total.toFloat())
}

// --------------------- 11-STEP SEGMENTATION ---------------------
fun Bitmap.segmentFruit(): Bitmap {
    // ✅ Step 0: Confirm fruit presence first
    if (this.isMostlyBackground()) {
        return this // Skip segmentation entirely
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

    // ✅ Optional: skip tiny detections
    if (output.width < width * 0.2 || output.height < height * 0.2) {
        return this
    }

    return output
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
        } else if (kotlin.math.abs(r - g) < grayTolerance &&
            kotlin.math.abs(g - b) < grayTolerance &&
            kotlin.math.abs(r - b) < grayTolerance
        ) {
            backgroundCount++
        }
    }
    return backgroundCount > width * height * ratio
}
