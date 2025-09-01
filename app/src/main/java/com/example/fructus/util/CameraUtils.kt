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

data class ClassificationResult(
    val label: String,
    val confidence: Float
)

// --------------------- FRUIT TYPE CLASSIFIER ---------------------

fun classifyFruit(bitmap: Bitmap, context: Context, threshold: Float = 0.90f): ClassificationResult {
    // ✅ Added Spoiled Mango
    val modelName = "fruit_type_model.tflite"
    val labels = listOf(
        "Cavendish",
        "Lakatan",
        "Mango",
        "Saba",
        "Spoiled Banana",
        "Spoiled Mango",
        "Spoiled Tomato",
        "Tomato"
    )

    val model = Interpreter(loadModelFile(context, modelName))
    val input = preprocessBitmap(bitmap)
    val output = Array(1) { FloatArray(labels.size) }

    model.run(input, output)
    model.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val confidence = if (maxIndex != -1) output[0][maxIndex] else 0f

    return if (maxIndex != -1 && confidence >= threshold) {
        ClassificationResult(labels[maxIndex], confidence)
    } else {
        ClassificationResult("No fruit detected", confidence)
    }
}

// --------------------- RIPENESS CLASSIFIER ---------------------

fun classifyRipeness(fruitType: String, bitmap: Bitmap, context: Context, threshold: Float = 0.7f): ClassificationResult {
    // ✅ Only classify ripeness if it's NOT spoiled
    if (fruitType.equals("Spoiled Banana", true)
        || fruitType.equals("Spoiled Tomato", true)
        || fruitType.equals("Spoiled Mango", true)
    ) {
        return ClassificationResult("Spoiled", 1f)
    }

    val modelName = when (fruitType.lowercase()) {
        "cavendish" -> "banana_cavendish_model.tflite"
        "lakatan" -> "banana_lakatan_model.tflite"
        "saba" -> "banana_saba_model.tflite"
        "mango" -> "mango_model.tflite"
        "tomato" -> "tomato_model.tflite"
        else -> return ClassificationResult("Unknown", 0f)
    }


    val labels = listOf("Overripe", "Ripe", "Unripe")
    val model = Interpreter(loadModelFile(context, modelName))
    val input = preprocessBitmap(bitmap)
    val output = Array(1) { FloatArray(labels.size) }

    model.run(input, output)
    model.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val confidence = if (maxIndex != -1) output[0][maxIndex] else 0f

    return if (maxIndex != -1 && confidence >= threshold) {
        ClassificationResult(labels[maxIndex], confidence)
    } else {
        ClassificationResult("Unknown", confidence)
    }
}

// --------------------- RIPENING STAGE MAPPER ---------------------

fun mapRipeningStage(fruitResult: ClassificationResult): String {
    return when {
        // ✅ Spoiled detected from fruit type
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
    return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
}

fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
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
