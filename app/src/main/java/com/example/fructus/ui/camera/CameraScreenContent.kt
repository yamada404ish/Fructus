package com.example.fructus.ui.camera

import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.fructus.ui.camera.components.HowToOverlay
import com.example.fructus.ui.camera.components.ScanAgain
import com.example.fructus.ui.shared.CustomBottomSheet
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
    detected: Boolean,
    detectedFruit: String,
    detectedRipeness: String,
    lifecycleOwner: LifecycleOwner,
    detectedState: MutableState<Boolean>,
    detectedFruitState: MutableState<String>,
    detectedRipenessState: MutableState<String>,
    onSaveFruit: (String, String, Boolean, Float, String?) -> Unit,
    onNavigateUp: () -> Unit,
    isDarkMode: Boolean,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 🔧 Core states
    val detectedConfidence = remember { mutableStateOf(0f) }
    val isSaved = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }
    val isScanning = remember { mutableStateOf(false) }
    val isBottomSheetVisible = remember { mutableStateOf(false) }
    val capturedImagePath = remember { mutableStateOf<String?>(null) }
    val isNaturalRipening = remember { mutableStateOf(true) }

    // ⚠️ Error Message States
    val showNoFruitDetected = remember { mutableStateOf(false) }
    val showDifferentFruitError = remember { mutableStateOf(false) }

    // Job to handle error timers (Debouncing)
    var errorJob by remember { mutableStateOf<Job?>(null) }

    // 🔹 MULTI-ANGLE STATE
    val multiAngleResults = remember { mutableStateListOf<ScanResult>() }
    val MAX_ANGLES = 5

    // 🔧 PreviewView ref
    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }

    // 🔍 Auto-focus helpers
    var lastAutoFocusTime by remember { mutableStateOf(0L) }
    val autoFocusCooldownMs = 1500L
    val scanBoxDp = 460f

    val clickGuard = remember { ClickGuard() }

    // 🔹 LOGIC: Combine results from multiple angles
    fun calculateCombinedResult() {
        if (multiAngleResults.isEmpty()) return

        // 1. Spoilage Check
        val spoiledEntry = multiAngleResults.firstOrNull {
            it.ripeness.contains("Spoiled", ignoreCase = true) ||
                    it.fruit.contains("Spoiled", ignoreCase = true)
        }

        if (spoiledEntry != null) {
            detectedFruitState.value = spoiledEntry.fruit
            detectedRipenessState.value = "Spoiled"
            detectedConfidence.value = spoiledEntry.confidence
            isNaturalRipening.value = false
            return
        }

        // 2. Fruit Type Vote
        val mostFrequentFruit = multiAngleResults.groupingBy { it.fruit }
            .eachCount().maxByOrNull { it.value }?.key ?: "Unknown"

        // 3. Ripeness Vote
        val relevantScans = multiAngleResults.filter { it.fruit == mostFrequentFruit }
        val ripenessScores = mutableMapOf<String, Float>()
        relevantScans.forEach {
            val current = ripenessScores.getOrDefault(it.ripeness, 0f)
            ripenessScores[it.ripeness] = current + it.confidence
        }
        val bestRipeness = ripenessScores.maxByOrNull { it.value }?.key ?: "Unknown"
        val avgConfidence = relevantScans.map { it.confidence }.average().toFloat()

        // 4. Natural/Artificial Vote
        val naturalCount = relevantScans.count { it.isNatural }
        val artificialCount = relevantScans.count { !it.isNatural }
        val finalIsNatural = naturalCount >= artificialCount

        detectedFruitState.value = mostFrequentFruit
        detectedRipenessState.value = bestRipeness
        detectedConfidence.value = avgConfidence
        isNaturalRipening.value = finalIsNatural
    }

    // 🔹 HELPER: Process Scan Results
    fun processScanResult(
        fruitLabel: String,
        ripenessLabel: String,
        confidence: Float,
        isNatural: Boolean,
        imagePath: String?
    ) {
        if (fruitLabel != "No fruit detected" && ripenessLabel != "Unknown") {
            // ✅ SUCCESS: FRUIT FOUND

            // 1. Cancel pending errors
            errorJob?.cancel()
            errorJob = null
            showNoFruitDetected.value = false
            showDifferentFruitError.value = false

            // 2. CHECK: Different Fruit?
            if (multiAngleResults.isNotEmpty()) {
                val originalFruit = multiAngleResults.first().fruit
                if (!fruitLabel.equals(originalFruit, ignoreCase = true)) {
                    isScanning.value = false // Stop scanning
                    showDifferentFruitError.value = true
                    errorJob = coroutineScope.launch {
                        delay(2000)
                        showDifferentFruitError.value = false
                    }
                    return
                }
            }

            // 3. Save Data
            isScanning.value = false // Stop scanning
            multiAngleResults.add(
                ScanResult(fruitLabel, ripenessLabel, confidence, isNatural)
            )
            calculateCombinedResult()

            if (capturedImagePath.value == null || imagePath != null) {
                capturedImagePath.value = imagePath
            }

            detectedState.value = true
            isBottomSheetVisible.value = true

        } else {
            // 🟥 FAILURE: NO FRUIT
            // Logic: Wait 0.8s. If still no fruit, show error and stop scanning.

            if (isScanning.value && !isBottomSheetVisible.value && !detectedState.value) {
                if (errorJob == null) {
                    errorJob = coroutineScope.launch {
                        delay(800) // Buffer delay

                        if (isScanning.value && !detectedState.value) {
                            showNoFruitDetected.value = true
                            isScanning.value = false // Stop scanning

                            delay(1000)
                            showNoFruitDetected.value = false
                        }
                        errorJob = null
                    }
                }
            }
        }
    }

    // 🔹 ACTION: Scan Another Angle
    fun scanAnotherAngle() {
        if (multiAngleResults.size >= MAX_ANGLES) {
            Toast.makeText(context, "Maximum $MAX_ANGLES angles allowed", Toast.LENGTH_SHORT).show()
            return
        }

        isBottomSheetVisible.value = false
        detectedState.value = false

        // Reset errors
        errorJob?.cancel()
        errorJob = null
        showNoFruitDetected.value = false
        showDifferentFruitError.value = false

        // Ensure scanning is OFF (User clicks button to start)
        isScanning.value = false
    }

    // 🔹 ACTION: Reset
    fun resetScan() {
        multiAngleResults.clear()
        detectedState.value = false
        detectedFruitState.value = ""
        detectedRipenessState.value = ""
        detectedConfidence.value = 0f
        isSaved.value = false
        isBottomSheetVisible.value = false
        isScanning.value = false

        // Reset dialogs
        showNoFruitDetected.value = false
        showDifferentFruitError.value = false
        capturedImagePath.value = null
        errorJob?.cancel()
        errorJob = null
    }

    fun parseRipeningMethod(label: String?): Boolean {
        val ripeningLabel = label?.trim()?.lowercase() ?: "unknown"
        return ripeningLabel == "natural"
    }

    fun triggerAutoFocus(camera: Camera?) {
        try {
            camera ?: return
            val previewView = previewViewRef.value ?: return
            val factory = SurfaceOrientedMeteringPointFactory(
                previewView.width.toFloat(), previewView.height.toFloat()
            )
            val density = context.resources.displayMetrics.density
            val boxPx = scanBoxDp * density
            val centerX = (previewView.width) / 2f
            val centerY = (previewView.height) / 2f
            val point = factory.createPoint(centerX, centerY)
            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(2, TimeUnit.SECONDS)
                .build()
            camera.cameraControl.startFocusAndMetering(action)
        } catch (e: Exception) { Log.e("AutoFocus", "Error", e) }
    }

    fun maybeAutoFocus(camera: Camera?) {
        val now = System.currentTimeMillis()
        if (now - lastAutoFocusTime > autoFocusCooldownMs) {
            triggerAutoFocus(camera)
            lastAutoFocusTime = now
        }
    }

    // 🖼️ Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                isScanning.value = true
                showNoFruitDetected.value = false

                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val fileName = "fruit_gallery_${System.currentTimeMillis()}"
                    val imagePath = saveBitmapToInternalStorage(context, bitmap, fileName)

                    val resultTriple = analyzeBitmap(bitmap, context)
                    val fruit = resultTriple.first
                    val ripeness = resultTriple.second
                    val ripeningMethod = resultTriple.third
                    val isNatural = parseRipeningMethod(ripeningMethod?.label)

                    processScanResult(
                        fruit.label,
                        ripeness?.label ?: "Unknown",
                        ripeness?.confidence ?: 0f,
                        isNatural,
                        imagePath
                    )
                }
            }
        }
    }

    val shelfLifeRange = getShelfLifeRange(detectedFruit, detectedRipeness, isNaturalRipening.value)
    val shelfLifeDisplay = if (shelfLifeRange.minDays == -1) "---" else formatShelfLifeRange(shelfLifeRange)

    val showScanAgainDialog = remember { mutableStateOf(false) }
    val showHowTo = remember { mutableStateOf(false) }

    val handleCancel: () -> Unit = {
        isBottomSheetVisible.value = false
        detectedState.value = false
        isSaved.value = false
    }

    BackHandler {
        when {
            showHowTo.value -> showHowTo.value = false
            isBottomSheetVisible.value -> handleCancel()
            else -> {
                cameraRef.value?.cameraControl?.enableTorch(false)
                flashEnabled.value = false
                onNavigateUp()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 📷 Camera Preview
        AndroidView(
            factory = {
                val previewView = PreviewView(it)
                previewViewRef.value = previewView

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224, 224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        // 🟢 RUNNING ON MAIN EXECUTOR (Your original "Fast" way)
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->
                            if (isScanning.value && !detectedState.value) {
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                try {
                                    val croppedForBox = bitmap.cropToScanBox(it)
                                    val rotatedBitmap = croppedForBox.rotate(imageProxy.imageInfo.rotationDegrees)

                                    val fruitResult = classifyFruit(rotatedBitmap, it)

                                    // Logic split: Found vs Not Found
                                    if (fruitResult.label != "No fruit detected") {
                                        val ripenessResult = classifyRipeness(fruitResult.label, rotatedBitmap, it)
                                        val ripeningMethodResult = classifyRipeningMethod(ripenessResult.label, rotatedBitmap, it)

                                        maybeAutoFocus(cameraRef.value)

                                        val isNatural = parseRipeningMethod(ripeningMethodResult.label)
                                        val fileName = "fruit_${System.currentTimeMillis()}"
                                        val imagePath = saveBitmapToInternalStorage(it, rotatedBitmap, fileName)

                                        // Call Helper
                                        processScanResult(
                                            fruitResult.label,
                                            ripenessResult.label,
                                            ripenessResult.confidence,
                                            isNatural,
                                            imagePath
                                        )
                                    } else {
                                        // Call Helper with "No fruit"
                                        processScanResult("No fruit detected", "Unknown", 0f, false, null)
                                    }
                                } catch (e: Exception) {
                                    Log.e("PredictionError", "Error", e)
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
                            lifecycleOwner, cameraSelector, preview, analyzer
                        )
                        cameraRef.value = camera
                    } catch (e: Exception) { Log.e("CameraX", "Error", e) }
                }, ContextCompat.getMainExecutor(it))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 🔦 Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isBottomSheetVisible.value) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(50.dp).safeClickable(clickGuard, coroutineScope) {
                        cameraRef.value?.cameraControl?.enableTorch(false)
                        flashEnabled.value = false
                        onNavigateUp()
                    },
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (!isBottomSheetVisible.value) {
                Icon(
                    painter = painterResource(if (flashEnabled.value) R.drawable.flash_on_button else R.drawable.flash_off_button),
                    contentDescription = "Flashlight",
                    modifier = Modifier.size(50.dp).safeClickable(clickGuard, coroutineScope, enabled = !showHowTo.value) {
                        cameraRef.value?.cameraControl?.enableTorch(!flashEnabled.value)
                        flashEnabled.value = !flashEnabled.value
                    },
                    tint = Color.Unspecified
                )
            }
        }

        // 📸 Bottom buttons
        if (!detected && !isScanning.value) {
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.gallery),
                    contentDescription = "Gallery",
                    modifier = Modifier.size(50.dp).safeClickable(clickGuard, coroutineScope, enabled = !showHowTo.value) {
                        cameraRef.value?.cameraControl?.enableTorch(false)
                        flashEnabled.value = false
                        showHowTo.value = false
                        galleryLauncher.launch("image/*")
                    },
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(R.drawable.camera_scan_icon),
                    contentDescription = "scan",
                    modifier = Modifier.size(100.dp).safeClickable(clickGuard, coroutineScope, enabled = !showHowTo.value) {
                        // Clear errors instantly on click
                        showNoFruitDetected.value = false
                        showDifferentFruitError.value = false
                        isScanning.value = true
                    },
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(R.drawable.howtouse),
                    contentDescription = "How to use",
                    modifier = Modifier.size(50.dp).safeClickable(clickGuard, coroutineScope, enabled = !showHowTo.value) {
                        showHowTo.value = true
                    },
                    tint = Color.Unspecified
                )
            }
        }

        // 📦 Scan box
        if (!isBottomSheetVisible.value && (isScanning.value || !detected)) {
            Icon(
                painter = painterResource(R.drawable.camera_scan_box),
                contentDescription = "scan",
                modifier = Modifier.align(Alignment.Center).size(460.dp),
                tint = Color.Unspecified
            )
        }

        // 🧠 Detection handling
        LaunchedEffect(detected, detectedFruit) {
            if (detected) {
                isBottomSheetVisible.value = !(detectedFruit == "No fruit detected" || detectedRipeness == "Unknown")
            }
        }

        if (detected) {
            when {
                detectedFruit == "No fruit detected" || detectedRipeness == "Unknown" -> {
                    // Logic handled in helper
                }
                isBottomSheetVisible.value -> {
                    AnimatedVisibility(
                        visible = isBottomSheetVisible.value,
                        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(900)),
                        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(900))
                    ) {
                        CustomBottomSheet(
                            fruitName = detectedFruit,
                            ripeningStage = detectedRipeness,
                            ripeningProcess = isNaturalRipening.value,
                            confidence = detectedConfidence.value,
                            shelfLifeRange = shelfLifeRange,
                            shelfLifeDisplay = shelfLifeDisplay,
                            isSaved = isSaved.value,
                            anglesScanned = multiAngleResults.size,
                            onScanAngle = { scanAnotherAngle() },
                            onSave = {
                                if (!isSaved.value) {
                                    onSaveFruit(
                                        detectedFruit,
                                        detectedRipeness,
                                        isNaturalRipening.value,
                                        detectedConfidence.value,
                                        capturedImagePath.value
                                    )
                                    isSaved.value = true
                                    showScanAgainDialog.value = true
                                }
                            },
                            onCancel = { resetScan() }
                        )
                    }
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

        // 🔴 ERROR MESSAGES
        if (showNoFruitDetected.value && !isBottomSheetVisible.value && !detectedState.value) {
            Text(
                "No fruit detected",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (showDifferentFruitError.value && !isBottomSheetVisible.value) {
            Text(
                "Different fruit detected",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (showScanAgainDialog.value) {
        ScanAgain(
            onYes = { resetScan() },
            onNo = {
                cameraRef.value?.cameraControl?.enableTorch(false)
                flashEnabled.value = false
                showScanAgainDialog.value = false
                onHome()
            },
            isDarkMode = isDarkMode
        )
    }

    if (showHowTo.value) {
        HowToOverlay(onDismiss = { showHowTo.value = false })
    }
}

data class ScanResult(
    val fruit: String,
    val ripeness: String,
    val confidence: Float,
    val isNatural: Boolean
)