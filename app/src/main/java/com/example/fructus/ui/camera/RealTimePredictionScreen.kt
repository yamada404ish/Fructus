package com.example.fructus.ui.screens.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import androidx.compose.ui.unit.dp

@Composable
fun RealTimePredictionScreen(context: Context, navController: NavController) {
    val lifecycleOwner = context as LifecycleOwner
    val permissionState = remember { mutableStateOf(false) }
    val localContext = LocalContext.current

    val detected = remember { mutableStateOf(false) }
    val detectedFruit = remember { mutableStateOf("") }
    val detectedRipeness = remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            permissionState.value = granted
            if (!granted) {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                permissionState.value = true
            }
            else -> launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!permissionState.value) {
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // HEADER
        Box(
            modifier = Modifier
                .weight(0.15f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Fruit Detection", style = MaterialTheme.typography.titleLarge)
        }

        // CAMERA PREVIEW
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxSize()
        ) {
            AndroidView(factory = {
                val previewView = PreviewView(it)

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(android.util.Size(224, 224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->
                            if (!detected.value) { // stop after first detection
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                val rotatedBitmap = bitmap.rotate(imageProxy.imageInfo.rotationDegrees)

                                try {
                                    val fruitType = classifyFruit(rotatedBitmap, it)
                                    val ripeness = classifyRipeness(fruitType, rotatedBitmap, it)

                                    detectedFruit.value = fruitType
                                    detectedRipeness.value = ripeness
                                    detected.value = true

                                    Log.d("Prediction", "Fruit: $fruitType, Ripeness: $ripeness")
                                } catch (e: Exception) {
                                    Log.e("PredictionError", "Error during classification", e)
                                }
                            }
                            imageProxy.close()
                        }
                    }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { prev ->
                        prev.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            analyzer
                        )
                    } catch (e: Exception) {
                        Log.e("CameraX", "Use case binding failed", e)
                        Toast.makeText(it, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }, ContextCompat.getMainExecutor(it))

                previewView
            })
        }

        // FOOTER
        Box(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (detected.value) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Detected: ${detectedFruit.value} - ${detectedRipeness.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        detected.value = false
                        detectedFruit.value = ""
                        detectedRipeness.value = ""
                    }) {
                        Text("Resume Scanning")
                    }
                }
            } else {
                Text("Scanning...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private fun classifyFruit(bitmap: Bitmap, context: Context): String {
    val modelName = "fruit_type_model.tflite"
    val labels = listOf("Cavendish Banana", "Lakatan Banana", "Tomato")

    val model = Interpreter(loadModelFile(context, modelName))
    val input = preprocessBitmap(bitmap)
    val output = Array(1) { FloatArray(labels.size) }

    model.run(input, output)
    model.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    return if (maxIndex != -1) labels[maxIndex] else "Unknown"
}

private fun classifyRipeness(fruitType: String, bitmap: Bitmap, context: Context): String {
    val modelName = when (fruitType.lowercase()) {
        "banana_lakatan" -> "banana_lakatan_model.tflite"
        "banana_cavendish" -> "banana_cavendish_model.tflite"
        "tomato" -> "tomato_model.tflite"
        else -> return "Unknown"
    }

    val labels = listOf("Unripe", "Ripe", "Overripe", "Spoiled")
    val model = Interpreter(loadModelFile(context, modelName))
    val input = preprocessBitmap(bitmap)
    val output = Array(1) { FloatArray(labels.size) }

    model.run(input, output)
    model.close()

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    return if (maxIndex != -1) labels[maxIndex] else "Unknown"
}

private fun loadModelFile(context: Context, modelFileName: String): ByteBuffer {
    val fileDescriptor = context.assets.openFd(modelFileName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
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
private fun ImageProxy.toBitmap(): Bitmap? {
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

private fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
