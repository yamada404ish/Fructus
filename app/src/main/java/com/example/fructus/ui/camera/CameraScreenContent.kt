//package com.example.fructus.ui.camera
//
//import android.util.Log
//import android.util.Size
//import android.widget.Toast
//import androidx.camera.core.Camera
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.slideInVertically
//import androidx.compose.animation.slideOutVertically
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.LifecycleOwner
//import com.example.fructus.R
//import com.example.fructus.ui.shared.CustomBottomSheet
//import com.example.fructus.ui.theme.poppinsFontFamily
//import com.example.fructus.util.FrontBackPrediction
//import com.example.fructus.util.calculateForegroundRatio
//import com.example.fructus.util.calculateHistogram
//import com.example.fructus.util.classifyFruit
//import com.example.fructus.util.classifyRipeness
//import com.example.fructus.util.combinePredictions
//import com.example.fructus.util.compareHistograms
//import com.example.fructus.util.formatShelfLifeRange
//import com.example.fructus.util.getShelfLifeRange
//import com.example.fructus.util.rotate
//import kotlinx.coroutines.delay
//
//enum class ScanPhase { IDLE, CAPTURING_FRONT, AWAIT_ROTATE, CAPTURING_BACK, PROCESSING }
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CameraScreenContent(
//    detected: Boolean,
//    detectedFruit: String,
//    detectedRipeness: String,
//
//    dtProcess: Boolean = true,
//    dtConfidence: Int = 90,
//
//    lifecycleOwner: LifecycleOwner,
//    detectedState: MutableState<Boolean>,
//    detectedFruitState: MutableState<String>,
//    detectedRipenessState: MutableState<String>,
//    onSaveFruit: (String, String, Boolean, Int) -> Unit,
//    onNavigateUp: () -> Unit,
//) {
//    val context = LocalContext.current
//
//    val isSaved = remember { mutableStateOf(false) }
//    val showSuccessMessage = remember { mutableStateOf(false) }
//    val flashEnabled = remember { mutableStateOf(false) }
//    val cameraRef = remember { mutableStateOf<Camera?>(null) }
//
//    val scanPhase = remember { mutableStateOf(ScanPhase.IDLE) }
//
//    val frontBitmap = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
//    val backBitmap = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
//    val frontHistogram = remember { mutableStateOf<IntArray?>(null) }
//    val backHistogram = remember { mutableStateOf<IntArray?>(null) }
//
//
//    val isScanning = remember { mutableStateOf(false) } // ✅ control scanning start
//
//
//    val isBottomSheetVisible = remember { mutableStateOf(false) }
//
//    val foregroundRatioThreshold = 0.03f
//    val histogramDifferenceThreshold = 0.15f
//
//    val shelfLifeRange = getShelfLifeRange(detectedFruit, detectedRipeness)
//    val shelfLifeDisplay = if (shelfLifeRange.minDays == -1) "---" else formatShelfLifeRange(shelfLifeRange)
//
//    // throttle toast messages
//    val lastToastTime = remember { mutableStateOf(0L) }
//    fun showThrottledToast(message: String) {
//        val now = System.currentTimeMillis()
//        if (now - lastToastTime.value > 2000) { // 2s throttle
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//            lastToastTime.value = now
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = {
//                val previewView = PreviewView(it)
//
//                val analyzer = ImageAnalysis.Builder()
//                    .setTargetResolution(Size(224, 224))
//                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build()
//                    .also { analysis ->
//                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->
//                            try {
//                                if (scanPhase.value == ScanPhase.CAPTURING_FRONT && !detectedState.value) {
//                                    val bitmap = imageProxy.toBitmap() ?: run {
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//                                    val rotatedBitmap = bitmap.rotate(imageProxy.imageInfo.rotationDegrees)
//
//                                    val fgRatio = calculateForegroundRatio(rotatedBitmap)
//                                    if (fgRatio < foregroundRatioThreshold) {
//                                        detectedFruitState.value = "No fruit detected"
//                                        detectedState.value = true
//                                        scanPhase.value = ScanPhase.IDLE // reset for retry
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//
//                                    val fruitFront = classifyFruit(rotatedBitmap, it)
//                                    if (fruitFront.label == "No fruit detected") {
//                                        detectedFruitState.value = "No fruit detected"
//                                        detectedState.value = true
//                                        scanPhase.value = ScanPhase.IDLE // reset for retry
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//
//                                    // valid fruit detected -> go rotate
//                                    frontBitmap.value = rotatedBitmap
//                                    frontHistogram.value = calculateHistogram(rotatedBitmap)
//                                    detectedState.value = false // clear "no fruit"
//                                    scanPhase.value = ScanPhase.AWAIT_ROTATE
//                                } else if (scanPhase.value == ScanPhase.CAPTURING_BACK && !detectedState.value) {
//                                    val bitmap = imageProxy.toBitmap() ?: run {
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//                                    val rotatedBitmap = bitmap.rotate(imageProxy.imageInfo.rotationDegrees)
//
//                                    val fgRatio = calculateForegroundRatio(rotatedBitmap)
//                                    if (fgRatio < foregroundRatioThreshold) {
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//
//                                    backBitmap.value = rotatedBitmap
//                                    backHistogram.value = calculateHistogram(rotatedBitmap)
//                                    scanPhase.value = ScanPhase.PROCESSING
//                                }
//
//                                if (scanPhase.value == ScanPhase.PROCESSING && !detectedState.value) {
//                                    val h1 = frontHistogram.value
//                                    val h2 = backHistogram.value
//                                    val similarity = compareHistograms(h1, h2)
//                                    val difference = 1f - similarity
//
//                                    if (difference < histogramDifferenceThreshold) {
//                                        scanPhase.value = ScanPhase.AWAIT_ROTATE
//                                        backBitmap.value = null
//                                        backHistogram.value = null
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//
//                                    val front = frontBitmap.value
//                                    val back = backBitmap.value
//                                    if (front == null || back == null) {
//                                        scanPhase.value = ScanPhase.IDLE
//                                        imageProxy.close(); return@setAnalyzer
//                                    }
//
//                                    try {
//                                        val fruitFront = classifyFruit(front, it)
//                                        val ripenessFront = classifyRipeness(fruitFront.label, front, it)
//
//                                        val fruitBack = classifyFruit(back, it)
//                                        val ripenessBack = classifyRipeness(fruitBack.label, back, it)
//
//                                        val frontPred = FrontBackPrediction(
//                                            fruitLabel = fruitFront.label,
//                                            fruitConfidence = fruitFront.confidence,
//                                            ripenessLabel = ripenessFront.label,
//                                            ripenessConfidence = ripenessFront.confidence
//                                        )
//
//                                        val backPred = FrontBackPrediction(
//                                            fruitLabel = fruitBack.label,
//                                            fruitConfidence = fruitBack.confidence,
//                                            ripenessLabel = ripenessBack.label,
//                                            ripenessConfidence = ripenessBack.confidence
//                                        )
//
//                                        val (combinedFruit, combinedRipeness) = combinePredictions(frontPred, backPred)
//
//                                        detectedFruitState.value = combinedFruit
//                                        detectedRipenessState.value = combinedRipeness
//                                        detectedState.value = true
//                                        isSaved.value = false
//                                        scanPhase.value = ScanPhase.IDLE
//                                    } catch (e: Exception) {
//                                        Log.e("PredictionError", "Error during combined classification", e)
//                                    } finally {
//                                        frontBitmap.value = null
//                                        backBitmap.value = null
//                                        frontHistogram.value = null
//                                        backHistogram.value = null
//                                    }
//                                }
//                            } catch (e: Exception) {
//                                Log.e("AnalyzerError", "Analyzer error", e)
//                            } finally {
//                                imageProxy.close()
//                            }
//                        }
//                    }
//
//                val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
//                cameraProviderFuture.addListener({
//                    val cameraProvider = cameraProviderFuture.get()
//                    val preview = Preview.Builder().build().also { prev ->
//                        prev.setSurfaceProvider(previewView.surfaceProvider)
//                    }
//                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//                    try {
//                        cameraProvider.unbindAll()
//                        val camera = cameraProvider.bindToLifecycle(
//                            lifecycleOwner,
//                            cameraSelector,
//                            preview,
//                            analyzer
//                        )
//                        cameraRef.value = camera
//                    } catch (e: Exception) {
//                        Log.e("CameraX", "Use case binding failed", e)
//                        Toast.makeText(it, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }, ContextCompat.getMainExecutor(it))
//
//                previewView
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        // Scan box overlay always visible
//        Icon(
//            painter = painterResource(R.drawable.camera_scan_box),
//            contentDescription = "camera scan",
//            modifier = Modifier.align(Alignment.Center).size(460.dp),
//            tint = Color.Unspecified
//        )
//
//        // Top bar
//        Row(
//            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
//                .padding(top = 50.dp, start = 16.dp, end = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Icon(
//                painter = painterResource(if (isBottomSheetVisible.value) R.drawable.camera_exit
//                else R
//                    .drawable
//                    .back_button),
//                contentDescription = if (isBottomSheetVisible.value) "Exit BottomSheet" else "Back",
//                modifier = Modifier.size(50.dp).clickable(
//                    onClick = {
//                        if (isBottomSheetVisible.value) {
//                            isBottomSheetVisible.value = false
//                            detectedState.value = false
//                            isSaved.value = false
//                        } else {
//                            onNavigateUp()
//                        }
//                    },
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ),
//                tint = Color.Unspecified
//            )
//            if (!isBottomSheetVisible.value) {
//
//                Icon(
//                    painter = painterResource(
//                        if (flashEnabled.value) R.drawable.flash_on_button else R.drawable.flash_off_button
//                    ),
//                    contentDescription = "Flashlight",
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clickable(
//                            onClick = {
//                                cameraRef.value?.cameraControl?.enableTorch(!flashEnabled.value)
//                                flashEnabled.value = !flashEnabled.value
//                            },
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        ),
//                    tint = Color.Unspecified
//                )
//            }
//        }
//
//        // Start button
//        if (!detected && scanPhase.value == ScanPhase.IDLE) {
//            Icon(
//                painter = painterResource(R.drawable.camera_scan_icon),
//                contentDescription = "Start 360 scan",
//                modifier = Modifier.align(Alignment.BottomCenter)
//                    .padding(bottom = 50.dp)
//                    .size(100.dp)
//                    .clickable(
//                        onClick = { scanPhase.value = ScanPhase.CAPTURING_FRONT },
//                        indication = null,
//                        interactionSource = remember { MutableInteractionSource() }
//                    ),
//                tint = Color.Unspecified
//            )
//        }
//
//        // Toast-like instructions (only after valid fruit)
//        LaunchedEffect(scanPhase.value) {
//            when (scanPhase.value) {
//                ScanPhase.CAPTURING_FRONT -> {
//                    showThrottledToast("Position front side close to the camera")
//                }
//                ScanPhase.AWAIT_ROTATE -> {
//                    if (frontBitmap.value != null) {
//                        showThrottledToast("Rotate the fruit slowly 180°")
//                    }
//                }
//                ScanPhase.CAPTURING_BACK -> {
//                    showThrottledToast("Position back side close to the camera")
//                }
//                ScanPhase.PROCESSING -> {
//                    showThrottledToast("Processing views...")
//                }
//                else -> {}
//            }
//        }
//
//        // Back capture button
//        if (scanPhase.value == ScanPhase.AWAIT_ROTATE) {
//            Icon(
//                painter = painterResource(R.drawable.camera_scan_icon),
//                contentDescription = "Capture back",
//                modifier = Modifier.align(Alignment.BottomCenter)
//                    .padding(bottom = 50.dp)
//                    .size(80.dp)
//                    .clickable(
//                        onClick = { scanPhase.value = ScanPhase.CAPTURING_BACK },
//                        indication = null,
//                        interactionSource = remember { MutableInteractionSource() }
//                    ),
//                tint = Color.Unspecified
//            )
//        }
//
//        LaunchedEffect(detected, detectedFruit) {
//            if (detected) {
//                isBottomSheetVisible.value = detectedFruit != "No fruit detected"
//            }
//        }
//
//        if (detected) {
//            if (detectedFruit == "No fruit detected") {
//                AnimatedVisibility(
//                    visible = detectedState.value,
//                    enter = fadeIn(animationSpec = tween(300)),
//                    exit = fadeOut(animationSpec = tween(300))
//                ) {
//                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text(
//                            "No fruit detected",
//                            fontFamily = poppinsFontFamily,
//                            fontWeight = FontWeight.Medium,
//                            fontSize = 20.sp,
//                            color = Color.Red
//                        )
//                    }
//                }
//                LaunchedEffect(detectedFruit) {
//                    delay(2000)
//                    detectedState.value = false
//                    scanPhase.value = ScanPhase.IDLE // reset to allow retry
//                }
//            } else if (isBottomSheetVisible.value) {
//                AnimatedVisibility(
//                    visible = isBottomSheetVisible.value,
//                    enter = slideInVertically(
//                        initialOffsetY = { fullHeight -> fullHeight },
//                        animationSpec = tween(400)
//                    ),
//                    exit = slideOutVertically(
//                        targetOffsetY = { fullHeight -> fullHeight },
//                        animationSpec = tween(400)
//                    )
//                ) {
//                    CustomBottomSheet(
//                        fruitName = detectedFruit,
//                        ripeningStage = detectedRipeness,
//                        ripeningProcess = dtProcess,
//                        confidence = dtConfidence,
//                        shelfLifeRange = shelfLifeRange,
//                        shelfLifeDisplay = shelfLifeDisplay,
//                        isSaved = isSaved.value,
//                        onSave = {
//                            if (!isSaved.value) {
//                                onSaveFruit(detectedFruit, detectedRipeness, dtProcess, dtConfidence)
//                                isSaved.value = true
//                                showSuccessMessage.value = true
//                                Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
//                            }
//                        },
//                    )
//                }
//            } else if (!isBottomSheetVisible.value) {
//
//                Icon(
//                    painter = painterResource(
//                        if (flashEnabled.value) R.drawable.flash_on_button else R.drawable.flash_off_button
//                    ),
//                    contentDescription = "Flashlight",
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clickable(
//                            onClick = {
//                                cameraRef.value?.cameraControl?.enableTorch(!flashEnabled.value)
//                                flashEnabled.value = !flashEnabled.value
//                            },
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        ),
//                    tint = Color.Unspecified
//                )
//            }
//        }
//    }
//}




// walang 360


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
import com.example.fructus.util.classifyFruit
import com.example.fructus.util.classifyRipeness
import com.example.fructus.util.formatShelfLifeRange
import com.example.fructus.util.getShelfLifeRange
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
    val isBottomSheetVisible = remember {mutableStateOf(false)}

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
                    .padding(bottom = 50.dp)
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
            if (detectedFruit == "No fruit detected"){
                AnimatedVisibility (
                    visible = detectedState.value,
                    enter = fadeIn(animationSpec = tween(100)),
                    exit = fadeOut(animationSpec = tween(100))
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
            }

            // temporary for unknown ripeness stage
            else if (detectedRipeness == "Unknown") {
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
            }

            // temporary for unknown ripeness stage

            else if (isBottomSheetVisible.value) {

                AnimatedVisibility(
                    visible = isBottomSheetVisible.value,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight }, // 👈 start offscreen
                        animationSpec = tween(durationMillis = 900)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight }, // 👈 slide down when hidden
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
                                onSaveFruit(detectedFruit, detectedRipeness, dtProcess, dtConfidence) // ✅
                                // call
                                // parent save function
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