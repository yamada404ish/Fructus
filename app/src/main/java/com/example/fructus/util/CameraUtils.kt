package com.example.fructus.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlin.math.min

data class DetectionResult(
    val label: String,
    val confidence: Float,
    val box: RectF
)

data class ClassificationResult(
    val label: String,
    val confidence: Float
)

// --------------------- YOLO + CNN PIPELINE ---------------------

fun detectAndClassifyFruit(
    bitmap: Bitmap,
    context: Context,
    threshold: Float = 0.5f
): Pair<ClassificationResult, ClassificationResult> {
    val detections = runYoloDetection(bitmap, context, threshold)

    if (detections.isEmpty()) {
        return Pair(
            ClassificationResult("No fruit detected", 0f),
            ClassificationResult("Unknown", 0f)
        )
    }

    // ✅ pick the biggest fruit box
    val bestBox = detections.maxByOrNull { it.box.width() * it.box.height() } ?: detections[0]

    // Crop safely
    val crop = cropBitmapSafe(bitmap, bestBox.box)

    val fruitResult = ClassificationResult(bestBox.label, bestBox.confidence)

    // Run CNN ripeness model
    val ripenessResult = classifyRipeness(fruitResult.label, crop, context)

    return Pair(fruitResult, ripenessResult)
}

// --------------------- YOLO DETECTOR ---------------------

fun runYoloDetection(bitmap: Bitmap, context: Context, threshold: Float = 0.5f): List<DetectionResult> {
    val interpreter = Interpreter(loadModelFile(context, "best_float16.tflite"))
    val inputSize = 640
    val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

    // input buffer
    val input = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3).apply {
        order(ByteOrder.nativeOrder())
    }
    val pixels = IntArray(inputSize * inputSize)
    resized.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
    for (px in pixels) {
        input.putFloat(((px shr 16) and 0xFF) / 255f)
        input.putFloat(((px shr 8) and 0xFF) / 255f)
        input.putFloat((px and 0xFF) / 255f)
    }

    // YOLO output shape [1,8400,9]
    val output = Array(1) { Array(8400) { FloatArray(9) } }
    interpreter.run(input, output)
    interpreter.close()

    val labels = listOf("saba", "cavendish", "lakatan", "tomato", "mango")
    val results = mutableListOf<DetectionResult>()

    for (i in 0 until 8400) {
        val row = output[0][i]
        val x = row[0]; val y = row[1]; val w = row[2]; val h = row[3]
        val obj = row[4]

        val classScores = row.copyOfRange(5, row.size)
        val maxIndex = classScores.indices.maxByOrNull { classScores[it] } ?: -1
        if (maxIndex == -1) continue
        val confidence = obj * classScores[maxIndex]

        if (confidence > threshold) {
            val label = labels[maxIndex]
            val left = max(0f, (x - w / 2f) * bitmap.width / inputSize)
            val top = max(0f, (y - h / 2f) * bitmap.height / inputSize)
            val right = min(bitmap.width - 1f, (x + w / 2f) * bitmap.width / inputSize)
            val bottom = min(bitmap.height - 1f, (y + h / 2f) * bitmap.height / inputSize)
            results.add(DetectionResult(label, confidence, RectF(left, top, right, bottom)))
        }
    }
    return nonMaxSuppression(results, 0.45f)
}

// --------------------- RIPENESS CLASSIFIER ---------------------

fun classifyRipeness(fruitType: String, bitmap: Bitmap, context: Context, threshold: Float = 0.7f): ClassificationResult {
    val modelName = when (fruitType.lowercase()) {
        "cavendish" -> "banana_cavendish_model.tflite"
        "lakatan" -> "banana_lakatan_model.tflite"
        "saba" -> "banana_saba_model.tflite"
        "mango" -> "mango_model.tflite"
        "tomato" -> "tomato_model.tflite"
        else -> return ClassificationResult("Unknown", 0f)
    }

    val labels = listOf("Overripe", "Ripe", "Unripe")
    val interpreter = Interpreter(loadModelFile(context, modelName))

    val input = preprocessBitmap(bitmap)
    val output = Array(1) { FloatArray(labels.size) }

    interpreter.run(input, output)
    interpreter.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val confidence = if (maxIndex != -1) output[0][maxIndex] else 0f

    return if (maxIndex != -1 && confidence >= threshold) {
        ClassificationResult(labels[maxIndex], confidence)
    } else {
        ClassificationResult("Unknown", confidence)
    }
}

// --------------------- HELPERS ---------------------

fun loadModelFile(context: Context, modelFileName: String): ByteBuffer {
    val fd = context.assets.openFd(modelFileName)
    val inputStream = FileInputStream(fd.fileDescriptor)
    val fileChannel = inputStream.channel
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
}

fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
    val inputSize = 224
    val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
    val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3).apply {
        order(ByteOrder.nativeOrder())
    }

    val pixels = IntArray(inputSize * inputSize)
    resized.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
    for (px in pixels) {
        buffer.putFloat(((px shr 16) and 0xFF) / 255f)
        buffer.putFloat(((px shr 8) and 0xFF) / 255f)
        buffer.putFloat((px and 0xFF) / 255f)
    }
    return buffer
}

fun cropBitmapSafe(src: Bitmap, rect: RectF): Bitmap {
    val left = rect.left.toInt().coerceAtLeast(0)
    val top = rect.top.toInt().coerceAtLeast(0)
    val right = rect.right.toInt().coerceAtMost(src.width)
    val bottom = rect.bottom.toInt().coerceAtMost(src.height)
    val width = (right - left).coerceAtLeast(1)
    val height = (bottom - top).coerceAtLeast(1)
    return Bitmap.createBitmap(src, left, top, width, height)
}

// --------------------- NMS ---------------------

fun nonMaxSuppression(detections: List<DetectionResult>, iouThreshold: Float): List<DetectionResult> {
    val sorted = detections.sortedByDescending { it.confidence }.toMutableList()
    val results = mutableListOf<DetectionResult>()
    while (sorted.isNotEmpty()) {
        val best = sorted.removeAt(0)
        results.add(best)
        val iterator = sorted.iterator()
        while (iterator.hasNext()) {
            if (iou(best.box, iterator.next().box) > iouThreshold) {
                iterator.remove()
            }
        }
    }
    return results
}

fun iou(a: RectF, b: RectF): Float {
    val areaA = (a.width()) * (a.height())
    val areaB = (b.width()) * (b.height())
    if (areaA <= 0 || areaB <= 0) return 0f

    val interLeft = max(a.left, b.left)
    val interTop = max(a.top, b.top)
    val interRight = min(a.right, b.right)
    val interBottom = min(a.bottom, b.bottom)
    val interArea = max(interRight - interLeft, 0f) * max(interBottom - interTop, 0f)
    return interArea / (areaA + areaB - interArea)
}

// --------------------- EXTENSIONS ---------------------

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

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
    val imageBytes = out.toByteArray()

    return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
