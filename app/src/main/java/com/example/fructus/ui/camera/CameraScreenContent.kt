package com.example.fructus.ui.camera

import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.fructus.ui.shared.BottomSheetInformation
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.classifyFruit
import com.example.fructus.util.classifyRipeness
import com.example.fructus.util.rotate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
//    val flashEnabled = remember { mutableStateOf(false) }

    val handleResumeScanning = {
        isSaved.value = false  // Reset save state when rescanning
        showSuccessMessage.value = false  // Hide success message
        onResumeScanning()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // CAMERA PREVIEW (Full screen background)
        AndroidView(
            factory = {
                val previewView = PreviewView(it)

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224, 224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->

                            if (!detectedState.value) {
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                val rotatedBitmap =
                                    bitmap.rotate(imageProxy.imageInfo.rotationDegrees)

                                try {
                                    val fruitType = classifyFruit(rotatedBitmap, it)
                                    val ripeness =
                                        classifyRipeness(fruitType, rotatedBitmap, it)

                                    detectedFruitState.value = fruitType
                                    detectedRipenessState.value = ripeness
                                    detectedState.value = true

                                    Log.d(
                                        "Prediction",
                                        "Fruit: $fruitType, Ripeness: $ripeness"
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
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            analyzer
                        )
                    } catch (e: Exception) {
                        Log.e("CameraX", "Use case binding failed", e)
                        Toast.makeText(it, "Camera error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, ContextCompat.getMainExecutor(it))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

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
                painter = painterResource(R.drawable.flash_off_button),
                contentDescription = "Flashlight",
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = Color.Unspecified
            )
        }

        // Overlay content when detected
        if (detected) {
            // Buttons positioned above the bottom sheet with success message
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp)
                    .padding(bottom = 280.dp + 16.dp), // sheetPeekHeight + some spacing
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RETRY BUTTON (Left side)
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

                // SUCCESS MESSAGE (Center) - Only show when saved
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
                    // Empty space to maintain layout balance when message is not shown
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // SAVE BUTTON (Right side)
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

                                    // Auto-hide message after 3 seconds
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

            // Bottom sheet (without buttons inside)
            BottomSheetInformation(
                fruitName = detectedFruit,
                ripeningStage = detectedRipeness,
                ripeningProcess = false,
                shelfLife = 3
            )

        } else {
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

@androidx.compose.ui.tooling.preview.Preview
@Composable
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