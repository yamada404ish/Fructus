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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.fructus.R
import com.example.fructus.ui.shared.CustomBottomSheet
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.classifyFruit
import com.example.fructus.util.classifyRipeness
import com.example.fructus.util.rotate

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

    val context = LocalContext.current

    val isSaved = remember { mutableStateOf(false) }
    val showSuccessMessage = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }
    val isScanning = remember { mutableStateOf(false) } // ✅ control scanning start
    val isBottomSheetVisible = remember {mutableStateOf(false)}

    val handleResumeScanning = {
        isSaved.value = false
        showSuccessMessage.value = false
        detectedState.value = false
        isScanning.value = true // resume scanning
        onResumeScanning()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // CAMERA PREVIEW
        AndroidView(
            factory = {
                val previewView = PreviewView(it)

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224, 224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->

                            // ✅ Only run analyzer when scanning is active
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
                                        classifyRipeness(fruitResult.label, rotatedBitmap, it)
                                    isSaved.value = false

                                    // ✅ assign label (String) instead of ClassificationResult
                                    detectedFruitState.value = fruitResult.label
                                    detectedRipenessState.value = ripenessResult.label

                                    detectedState.value = true
                                    isScanning.value = false // stop scanning after detect

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
                    if (isBottomSheetVisible.value) R.drawable.camera_exit else R.drawable
                        .back_button
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
                    .padding(bottom = 100.dp)
                    .size(100.dp)
                    .clickable(
                        onClick = { isScanning.value = true },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint  = Color.Unspecified

            )
        }

        if (!isBottomSheetVisible.value && (isScanning.value || !detected)) {
            Icon(
                painter = painterResource(R.drawable.camera_scan_box),
                contentDescription = "camera scan",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(360.dp),
                tint = Color.Unspecified
            )
        }

        LaunchedEffect(detected, detectedFruit) {
            if (detected) {
                if (detectedFruit == "No fruit detected") {
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
            if (detectedFruit == "No fruit detected"){
                AnimatedVisibility (
                    visible = detectedState.value,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center // 👈 centers inside full screen
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
            } else if (isBottomSheetVisible.value) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 0.5f)) // 👈 semi-transparent
//                        .clickable(
//                            onClick = {}, // close when tapping outside
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        )
//                )

//                BottomSheetInformation(
//                    fruitName = detectedFruit,
//                    ripeningStage = detectedRipeness,
//                    ripeningProcess = false,
//                    shelfLife = 3
//                )


                // uncomment to use the new bottom sheet
//                CustomBottomSheet(
//                   fruitName = detectedFruit,
//                   ripeningStage = detectedRipeness,
//                   ripeningProcess = false,
//                   shelfLife = 3,
//                   confidence = 90,
//                   isSaved = isSaved.value,
//                   onSave = {
//                       if (!isSaved.value) {
//                           onSaveFruit(detectedFruit, detectedRipeness) // ✅ call parent save function
//                           isSaved.value = true
//                           showSuccessMessage.value = true
//
//                           CoroutineScope(Dispatchers.Main).launch {
//                               delay(3000)
//                               showSuccessMessage.value = false
//                           }
//                       }
//                   }
//               )

                AnimatedVisibility(
                    visible = isBottomSheetVisible.value,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight }, // 👈 start offscreen
                        animationSpec = tween(durationMillis = 9000)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight }, // 👈 slide down when hidden
                        animationSpec = tween(durationMillis = 9000)
                    )
                ) {
                    CustomBottomSheet(
                        fruitName = detectedFruit,
                        ripeningStage = detectedRipeness,
                        ripeningProcess = false,
                        shelfLife = 3,
                        confidence = 90,
                        isSaved = isSaved.value,
                        onSave = {
                            if (!isSaved.value) {
                                onSaveFruit(detectedFruit, detectedRipeness) // ✅ call parent save function
                                isSaved.value = true
                                showSuccessMessage.value = true

//                                CoroutineScope(Dispatchers.Main).launch {
//                                    delay(3000)
//                                    showSuccessMessage.value = false
//                                }

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
            // ✅ Show scanning status while analyzer is active
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
