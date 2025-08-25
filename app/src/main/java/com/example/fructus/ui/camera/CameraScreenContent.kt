package com.example.fructus.ui.camera

import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.fructus.R
import com.example.fructus.ui.camera.composable.BottomSheetInformation
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.ClassificationResult
import com.example.fructus.util.classifyFruit
import com.example.fructus.util.classifyRipeness
import com.example.fructus.util.rotate
import com.example.fructus.util.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
    detected: Boolean,
    detectedFruit: String,
    detectedRipeness: String,
    onResumeScanning: () -> Unit,
    lifecycleOwner: LifecycleOwner,
    detectedState: MutableState<Boolean>,
    detectedFruitState: MutableState<String>,
    detectedRipenessState: MutableState<String>,
    onSaveFruit: (String, String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val isSaved = remember { mutableStateOf(false) }
    val showSuccessMessage = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }
    val isScanning = remember { mutableStateOf(false) }

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }

    val handleResumeScanning = {
        isSaved.value = false
        showSuccessMessage.value = false
        detectedState.value = false
        isScanning.value = true
        onResumeScanning()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // CAMERA PREVIEW
        AndroidView(
            factory = {
                val previewView = PreviewView(it)

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224, 224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->

                            if (isScanning.value && !detectedState.value) {
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                val rotatedBitmap =
                                    bitmap.rotate(imageProxy.imageInfo.rotationDegrees)

                                try {
                                    val fruitResult = classifyFruit(rotatedBitmap, it)
                                    val ripenessResult =
                                        if (fruitResult.label.contains("Spoiled", true)) {
                                            ClassificationResult("Spoiled", 1.0f)
                                        } else {
                                            classifyRipeness(fruitResult.label, rotatedBitmap, it)
                                        }

                                    CoroutineScope(Dispatchers.Main).launch {
                                        detectedFruitState.value = fruitResult.label
                                        detectedRipenessState.value = ripenessResult.label
                                        detectedState.value = true
                                        isScanning.value = false
                                    }

                                    Log.d(
                                        "Prediction",
                                        "Fruit: ${fruitResult.label} (${fruitResult.confidence}), " +
                                                "Ripeness: ${ripenessResult.label} (${ripenessResult.confidence})"
                                    )
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
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            analyzer
                        )
                        cameraRef.value = camera
                    } catch (e: Exception) {
                        Log.e("CameraX", "Use case binding failed", e)
                        Toast.makeText(it, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }, ContextCompat.getMainExecutor(it))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.back_button),
                contentDescription = "Back",
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        onClick = onNavigateUp,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = Color.Unspecified
            )

            Icon(
                painter = painterResource(
                    if (flashEnabled.value) R.drawable.flash_on_button else R.drawable.flash_off_button
                ),
                contentDescription = "Flashlight",
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        onClick = {
                            cameraRef.value?.cameraControl?.enableTorch(!flashEnabled.value)
                            flashEnabled.value = !flashEnabled.value
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = Color.Unspecified
            )
        }

        // START SCAN BUTTON
        if (!detected && !isScanning.value) {
            Button(
                onClick = { isScanning.value = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            ) {
                Text("Start Scan")
            }
        }

        // DETECTED OVERLAY (Retry + Save + Message + BottomSheet)
        if (detected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp)
                    .padding(bottom = 280.dp + 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RETRY
                Icon(
                    painter = painterResource(R.drawable.retry_button),
                    contentDescription = "Retry",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            onClick = handleResumeScanning,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )

                // SUCCESS MESSAGE
                if (showSuccessMessage.value) {
                    Text(
                        "Saved Successfully!",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // SAVE
                Icon(
                    painter = painterResource(
                        if (isSaved.value) R.drawable.save_on_button
                        else R.drawable.save_off_button
                    ),
                    contentDescription = if (isSaved.value) "Saved" else "Save",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            enabled = !isSaved.value,
                            onClick = {
                                if (!isSaved.value) {
                                    onSaveFruit(detectedFruit, detectedRipeness)
                                    isSaved.value = true
                                    showSuccessMessage.value = true

                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(3000)
                                        showSuccessMessage.value = false
                                    }
                                }
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
            }

            // BOTTOM SHEET (with spoiled check logic)
            BottomSheetInformation(
                fruitName = detectedFruit,
                ripeningStage = detectedRipeness,
                ripeningProcess = false,
                shelfLife = if (detectedRipeness.equals("Spoiled", true)) 0 else 3
            )
        } else if (isScanning.value) {
            Text(
                "Scanning...",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
private fun CameraScreenPrev() {
    FructusTheme {
        CameraScreenContent(
            detected = false,
            detectedFruit = "",
            detectedRipeness = "",
            onResumeScanning = {},
            lifecycleOwner = LocalLifecycleOwner.current,
            detectedState = remember { mutableStateOf(false) },
            detectedFruitState = remember { mutableStateOf("") },
            detectedRipenessState = remember { mutableStateOf("") },
            onSaveFruit = { _, _ -> },
            onNavigateUp = {}
        )
    }
}
