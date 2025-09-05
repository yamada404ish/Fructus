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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.fructus.R
import com.example.fructus.ui.shared.CustomBottomSheet
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.detectAndClassifyFruit
import com.example.fructus.util.formatShelfLifeRange
import com.example.fructus.util.getShelfLifeRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.fructus.util.rotate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
    detected: Boolean,
    detectedFruit: String,
    detectedRipeness: String,

    // ✅ Placeholder values for now
    dtProcess: Boolean = true,
    dtConfidence: Int = 90,

    lifecycleOwner: LifecycleOwner,
    detectedState: MutableState<Boolean>,
    detectedFruitState: MutableState<String>,
    detectedRipenessState: MutableState<String>,
    onSaveFruit: (String, String, Boolean, Int) -> Unit,
    onNavigateUp: () -> Unit,
) {

    val context = LocalContext.current

    val isSaved = remember { mutableStateOf(false) }
    val showSuccessMessage = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }
    val isScanning = remember { mutableStateOf(false) } // ✅ control scanning start
    val isBottomSheetVisible = remember { mutableStateOf(false) }

    // 🔎 Shelf life check (centralized in util)
    val shelfLifeRange = getShelfLifeRange(detectedFruit, detectedRipeness)
    val shelfLifeDisplay = if (shelfLifeRange.minDays == -1) {
        "---"
    } else {
        formatShelfLifeRange(shelfLifeRange)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // CAMERA PREVIEW
        AndroidView(
            factory = {
                val previewView = PreviewView(it)

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(640, 640)) // YOLO expects 640; we still convert from ImageProxy to Bitmap
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->
                            // Only run analyzer when scanning is active
                            if (isScanning.value && !detectedState.value) {
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                // rotate to correct orientation
                                val rotatedBitmap = bitmap.rotate(imageProxy.imageInfo.rotationDegrees.toFloat())


                                // run YOLO -> crop -> CNN in background coroutine to avoid blocking UI thread
                                CoroutineScope(Dispatchers.Default).launch {
                                    try {
                                        val (fruitRes, ripenessRes) = detectAndClassifyFruit(rotatedBitmap, it)

                                        // update UI state on main thread
                                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                                            isSaved.value = false
                                            detectedFruitState.value = fruitRes.label
                                            detectedRipenessState.value = ripenessRes.label
                                            detectedState.value = true
                                            isScanning.value = false

                                            Log.d(
                                                "Prediction",
                                                "Fruit: ${fruitRes.label} (${fruitRes.confidence}), " +
                                                        "Ripeness: ${ripenessRes.label} (${ripenessRes.confidence})"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        Log.e("PredictionError", "Error during detection/classification", e)
                                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                                            detectedFruitState.value = "No fruit detected"
                                            detectedRipenessState.value = "Unknown"
                                            detectedState.value = true
                                            isScanning.value = false
                                        }
                                    } finally {
                                        imageProxy.close()
                                    }
                                }
                            } else {
                                imageProxy.close()
                            }
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
                        cameraRef.value = camera // ✅ keep reference for flashlight toggle
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

        // Top bar (Back + Flash)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(
                    if (isBottomSheetVisible.value) R.drawable.camera_exit else R.drawable.back_button
                ),
                contentDescription = if (isBottomSheetVisible.value) "Exit BottomSheet" else "Back",
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        onClick = {
                            if (isBottomSheetVisible.value) {
                                isBottomSheetVisible.value = false
                                detectedState.value = false
                                isSaved.value = false
                            } else {
                                onNavigateUp()
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = Color.Unspecified
            )

            if (!isBottomSheetVisible.value) {
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
        }

        // ✅ Start Scan button (only show if not detected & not scanning)
        if (!detected && !isScanning.value) {
            Icon(
                painter = painterResource(R.drawable.camera_scan_icon),
                contentDescription = "camera icon",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
                    .size(100.dp)
                    .clickable(
                        onClick = { isScanning.value = true },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = Color.Unspecified
            )
        }

        if (!isBottomSheetVisible.value && (isScanning.value || !detected)) {
            Icon(
                painter = painterResource(R.drawable.camera_scan_box),
                contentDescription = "camera scan",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(460.dp),
                tint = Color.Unspecified
            )
        }

        LaunchedEffect(detected, detectedFruit) {
            if (detected) {
                if (detectedFruit == "No fruit detected" || detectedRipeness == "Unknown") {
                    // ❌ Don't open bottom sheet, just show message
                    isBottomSheetVisible.value = false
                } else {
                    // ✅ Valid fruit detected -> open bottom sheet
                    isBottomSheetVisible.value = true
                }
            }
        }

        // Overlay when detected
        if (detected) {
            if (detectedFruit == "No fruit detected") {
                AnimatedVisibility(
                    visible = detectedState.value,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No fruit detected",
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color.Red
                        )
                    }
                }
                LaunchedEffect(detectedFruit) {
                    kotlinx.coroutines.delay(2000)
                    detectedState.value = false
                    isScanning.value = false
                }
            } else if (detectedRipeness == "Unknown") {
                AnimatedVisibility(
                    visible = detectedState.value,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Try again",
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color.Green
                        )
                    }
                }
                LaunchedEffect(detectedFruit) {
                    kotlinx.coroutines.delay(2000)
                    detectedState.value = false
                    isScanning.value = false
                }
            } else if (isBottomSheetVisible.value) {
                AnimatedVisibility(
                    visible = isBottomSheetVisible.value,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 900)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 900)
                    )
                ) {
                    CustomBottomSheet(
                        fruitName = detectedFruit,
                        ripeningStage = detectedRipeness,
                        ripeningProcess = dtProcess,
                        confidence = dtConfidence,
                        shelfLifeRange = shelfLifeRange,
                        shelfLifeDisplay = shelfLifeDisplay,
                        isSaved = isSaved.value,
                        onSave = {
                            if (!isSaved.value) {
                                onSaveFruit(detectedFruit, detectedRipeness, dtProcess, dtConfidence)
                                isSaved.value = true
                                showSuccessMessage.value = true

                                Toast.makeText(
                                    context,
                                    "Saved Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )
                }
            }
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

