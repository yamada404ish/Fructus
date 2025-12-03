package com.example.fructus.ui.camera

import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.fructus.ui.camera.components.GoToGallery

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

    val fromGallery = remember { mutableStateOf(false) }

    val colors = MaterialTheme.appColors

    // 🔧 Core states
    val detectedConfidence = remember { mutableStateOf(0f) }
    val isSaved = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }
    val isScanning = remember { mutableStateOf(false) }
    val isBottomSheetVisible = remember { mutableStateOf(false) }
    val capturedImagePath = remember { mutableStateOf<String?>(null) }
    val showNoFruitDetected = remember { mutableStateOf(false) }
    val isNaturalRipening = remember { mutableStateOf(true) }

    // 🆕 Scan Another Angle states
    val isScanningAnotherAngle = remember { mutableStateOf(false) }
    val originalScannedFruit = remember { mutableStateOf<String?>(null) }
    val angleScansCount = remember { mutableStateOf(0) }
    val showDifferentFruitMessage = remember { mutableStateOf(false) }

    // Store all angle scan results to find the highest confidence
    data class AngleScanResult(
        val fruit: String,
        val ripeness: String,
        val confidence: Float,
        val isNatural: Boolean,
        val imagePath: String?
    )
    val angleScanResults = remember { mutableStateListOf<AngleScanResult>() }

    // 🔧 PreviewView ref (required for metering point factory)
    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }

    // 🔍 Auto-focus helpers
    var lastAutoFocusTime by remember { mutableStateOf(0L) }
    val autoFocusCooldownMs = 1500L
    val scanBoxDp = 460f

    // Helper function to safely parse ripening method
    fun parseRipeningMethod(label: String?): Boolean {
        val ripeningLabel = label?.trim()?.lowercase() ?: "unknown"
        return when (ripeningLabel) {
            "natural" -> true
            "artificial" -> false
            else -> {
                Log.w("RipeningMethod", "Unexpected label: $label")
                false
            }
        }
    }

    val clickGuard = remember { ClickGuard() }
    val coroutineScope = rememberCoroutineScope()

    // 🔎 Trigger autofocus (center of the scan box)
    fun triggerAutoFocus(camera: Camera?) {
        try {
            camera ?: return
            val cameraControl = camera.cameraControl

            val previewView = previewViewRef.value ?: return

            val factory = SurfaceOrientedMeteringPointFactory(
                previewView.width.toFloat(),
                previewView.height.toFloat()
            )

            val density = context.resources.displayMetrics.density
            val boxPx = scanBoxDp * density

            val left = (previewView.width - boxPx) / 2f
            val top = (previewView.height - boxPx) / 2f
            val centerX = left + (boxPx / 2f)
            val centerY = top + (boxPx / 2f)

            val point = factory.createPoint(centerX, centerY)

            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(2, TimeUnit.SECONDS)
                .build()

            cameraControl.startFocusAndMetering(action)
            Log.d("AutoFocus", "Triggered AF at scan-box center: x=$centerX y=$centerY")
        } catch (e: Exception) {
            Log.e("AutoFocus", "Error triggering autofocus", e)
        }
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
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            fromGallery.value = true

            bitmap?.let { selectedImage ->
                isScanning.value = true
                showNoFruitDetected.value = false

                // 🆕 Reset angle scanning state for gallery scans
                isScanningAnotherAngle.value = false
                originalScannedFruit.value = null
                angleScansCount.value = 0
                angleScanResults.clear()
                showDifferentFruitMessage.value = false

                try {
                    val fileName = "fruit_gallery_${System.currentTimeMillis()}"
                    val imagePath = saveBitmapToInternalStorage(context, selectedImage, fileName)
                    capturedImagePath.value = imagePath

                    val fruitResult = classifyFruit(selectedImage, context)
                    val ripenessResult = classifyRipeness(fruitResult.label, selectedImage, context)
                    val ripeningMethodResult = classifyRipeningMethod(
                        ripenessResult.label,
                        selectedImage,
                        context
                    )

                    isNaturalRipening.value = parseRipeningMethod(ripeningMethodResult.label)

                    detectedFruitState.value = fruitResult.label
                    detectedRipenessState.value = ripenessResult.label
                    detectedConfidence.value = ripenessResult.confidence
                    detectedState.value = true

                    if (fruitResult.label != "No fruit detected" && ripenessResult.label != "Unknown") {
                        isBottomSheetVisible.value = true
                    } else {
                        isBottomSheetVisible.value = false
                        showNoFruitDetected.value = true
                    }

                    Log.d(
                        "GalleryDetection",
                        "Fruit: ${fruitResult.label}, Ripeness: ${ripenessResult.label}, Confidence: ${ripenessResult.confidence}"
                    )
                } catch (e: Exception) {
                    Log.e("GalleryError", "Error during gallery classification", e)
                    showNoFruitDetected.value = true
                } finally {
                    isScanning.value = false
                }
            }
        }
    }

    // 🍌 Shelf life info
    val shelfLifeRange = getShelfLifeRange(detectedFruit, detectedRipeness, isNaturalRipening.value)
    val shelfLifeDisplay =
        if (shelfLifeRange.minDays == -1) "---" else formatShelfLifeRange(shelfLifeRange)

    val showScanAgainDialog = remember { mutableStateOf(false) }
    val showHowTo = remember { mutableStateOf(false) }
    val showGoToGalleryDialog = remember { mutableStateOf(false) }
    MaterialTheme.appColors

    val handleCancel: () -> Unit = {
        isBottomSheetVisible.value = false
        detectedState.value = false
        isSaved.value = false

        // Reset angle scanning states
        isScanningAnotherAngle.value = false
        originalScannedFruit.value = null
        angleScansCount.value = 0
        angleScanResults.clear()
        showDifferentFruitMessage.value = false
    }

    // 🆕 Handle Scan Another Angle
    val handleScanAnotherAngle: () -> Unit = {
        if (angleScansCount.value >= 5) {
            Toast.makeText(context, "Scanning different angle reach limit", Toast.LENGTH_SHORT).show()
        } else {
            // Store the original fruit if this is the first angle scan
            if (!isScanningAnotherAngle.value) {
                originalScannedFruit.value = detectedFruit
                // Store the initial scan result
                angleScanResults.add(
                    AngleScanResult(
                        fruit = detectedFruit,
                        ripeness = detectedRipeness,
                        confidence = detectedConfidence.value,
                        isNatural = isNaturalRipening.value,
                        imagePath = capturedImagePath.value
                    )
                )
            }

            isScanningAnotherAngle.value = true
            isBottomSheetVisible.value = false
            detectedState.value = false
//            isScanning.value = true
            angleScansCount.value += 1
            showDifferentFruitMessage.value = false
        }
    }

    // 🪟 Back button behavior
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

    // ⚙️ FIX: Move resetScan function up so it can be called by the scan button
    fun resetScan() {
        // normal scan reset
        isScanning.value = false
        detectedState.value = false
        detectedFruitState.value = ""
        detectedRipenessState.value = ""
        detectedConfidence.value = 0f
        isSaved.value = false

        isBottomSheetVisible.value = false
        showNoFruitDetected.value = false

        // 🔥 DO NOT RESET angle scanning here when scanning another angle
        if (!isScanningAnotherAngle.value) {
            // only reset these when starting a completely new scan
            isScanningAnotherAngle.value = false
            originalScannedFruit.value = null
            angleScanResults.clear()
            angleScansCount.value = 0
            showDifferentFruitMessage.value = false
        }

        capturedImagePath.value = null

        Log.d("SCAN_RESET", "Reset done (angle mode preserved: ${isScanningAnotherAngle.value})")
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
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->

                            // Guard clause to stop analysis if a photo is captured/analyzed and we're not angle scanning
                            if (capturedImagePath.value != null && !isScanningAnotherAngle.value) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            if (isScanning.value && !detectedState.value) {
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                try {
                                    val croppedForBox = bitmap.cropToScanBox(it)
                                    val rotatedBitmap =
                                        croppedForBox.rotate(imageProxy.imageInfo.rotationDegrees)

                                    val fruitResult = classifyFruit(rotatedBitmap, it)

                                    // 🆕 Check if scanning another angle and fruit is different
                                    if (isScanningAnotherAngle.value &&
                                        originalScannedFruit.value != null &&
                                        fruitResult.label != "No fruit detected" &&
                                        fruitResult.label != originalScannedFruit.value) {

                                        showDifferentFruitMessage.value = true
                                        isScanning.value = false

                                        imageProxy.close()
                                        return@setAnalyzer
                                    }

                                    val ripenessResult =
                                        classifyRipeness(fruitResult.label, rotatedBitmap, it)
                                    val ripeningMethodResult =
                                        classifyRipeningMethod(ripenessResult.label, rotatedBitmap, it)

                                    if (fruitResult.label != "No fruit detected") {
                                        maybeAutoFocus(cameraRef.value)
                                    }

                                    isNaturalRipening.value = parseRipeningMethod(ripeningMethodResult.label)
                                    isSaved.value = false

                                    val fileName = "fruit_${System.currentTimeMillis()}"
                                    val imagePath = saveBitmapToInternalStorage(
                                        it,
                                        rotatedBitmap,
                                        fileName
                                    )
                                    capturedImagePath.value = imagePath

                                    detectedFruitState.value = fruitResult.label
                                    detectedRipenessState.value = ripenessResult.label
                                    detectedConfidence.value = ripenessResult.confidence

                                    // 🆕 If scanning another angle, store the result
                                    if (isScanningAnotherAngle.value &&
                                        fruitResult.label != "No fruit detected" &&
                                        ripenessResult.label != "Unknown") {
                                        angleScanResults.add(
                                            AngleScanResult(
                                                fruit = fruitResult.label,
                                                ripeness = ripenessResult.label,
                                                confidence = ripenessResult.confidence,
                                                isNatural = isNaturalRipening.value,
                                                imagePath = imagePath
                                            )
                                        )
                                    }

                                    detectedState.value = true
                                    isScanning.value = false
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
                            lifecycleOwner, cameraSelector, preview, analyzer
                        )
                        cameraRef.value = camera
                    } catch (e: Exception) {
                        Log.e("CameraX", "Use case binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(it))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 🔦 Top bar (Back + Flash)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Icon
                if (!isBottomSheetVisible.value) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(50.dp)
                            .safeClickable(clickGuard, coroutineScope) {
                                cameraRef.value?.cameraControl?.enableTorch(false)
                                flashEnabled.value = false
                                onNavigateUp()
                            },
                        tint = Color.Unspecified
                    )
                } else {
                    Spacer(modifier = Modifier.width(50.dp))
                }

                // Cancel Button in the middle
                if (isScanningAnotherAngle.value && !isBottomSheetVisible.value) {
                    Button(
                        onClick = {
                            // Cancel scanning another angle
                            isScanningAnotherAngle.value = false
                            angleScansCount.value = 0
                            angleScanResults.clear()
                            showDifferentFruitMessage.value = false

                            // Reset detection
                            detectedState.value = false
                            detectedFruitState.value = ""
                            detectedRipenessState.value = ""
                            detectedConfidence.value = 0f
                            isBottomSheetVisible.value = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.button),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            "Cancel Scanning",
                            fontFamily = poppinsFontFamily,
                            fontSize = responsiveSp(8, 10, 12),
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Flash Icon
                if (!isBottomSheetVisible.value) {
                    Icon(
                        painter = painterResource(
                            if (flashEnabled.value) R.drawable.flash_on_button else R.drawable.flash_off_button
                        ),
                        contentDescription = "Flashlight",
                        modifier = Modifier
                            .size(50.dp)
                            .safeClickable(
                                clickGuard,
                                coroutineScope,
                                enabled = !showHowTo.value
                            ) {
                                cameraRef.value?.cameraControl?.enableTorch(!flashEnabled.value)
                                flashEnabled.value = !flashEnabled.value
                            },
                        tint = Color.Unspecified
                    )
                } else {
                    Spacer(modifier = Modifier.width(50.dp))
                }
            }
        }



        // 📸 Bottom buttons
        if (!detected && !isScanning.value) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.gallery),
                    contentDescription = "Gallery",
                    modifier = Modifier
                        .size(50.dp)
                        .safeClickable(
                            clickGuard,
                            coroutineScope,
                            enabled = !showHowTo.value
                        ) {
                            cameraRef.value?.cameraControl?.enableTorch(false)
                            flashEnabled.value = false
                            showHowTo.value = false

                            // 🆕 Check if in angle scanning mode
                            if (isScanningAnotherAngle.value) {
                                showGoToGalleryDialog.value = true
                            } else {
                                galleryLauncher.launch("image/*")
                            }
                        },
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(R.drawable.camera_scan_icon),
                    contentDescription = "camera icon",
                    modifier = Modifier
                        .size(100.dp)
                        .safeClickable(
                            clickGuard,
                            coroutineScope,
                            enabled = !showHowTo.value
                        ) {
                            // 🔥 FIX: Reset all previous detection states before scanning again
                            fromGallery.value = false
                            resetScan()
                            isScanning.value = true

                        },
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(R.drawable.howtouse),
                    contentDescription = "How to use",
                    modifier = Modifier
                        .size(50.dp)
                        .safeClickable(
                            clickGuard,
                            coroutineScope,
                            enabled = !showHowTo.value
                        ) {
                            showHowTo.value = true
                        },
                    tint = Color.Unspecified
                )
            }
        }

        // 📦 Scan box overlay
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

        // 🧠 Detection handling
        LaunchedEffect(detected, detectedFruit) {
            if (detected) {
                isBottomSheetVisible.value =
                    !(detectedFruit == "No fruit detected" || detectedRipeness == "Unknown")
            }
        }

        if (detected) {
            when {
                detectedFruit == "No fruit detected" || detectedRipeness == "Unknown" -> {
                    Text(
                        "No fruit detected",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    LaunchedEffect(detectedFruit) {
                        delay(800)
                        detectedState.value = false
                        isScanning.value = false
                        showNoFruitDetected.value = false

                        // Resume angle scanning if active
                        if (isScanningAnotherAngle.value && angleScansCount.value < 5) {
                            delay(200)
                            isScanning.value = true
                        }
                    }
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
                            onSave = {
                                if (!isSaved.value) {
                                    // 🆕 Find the highest confidence result
                                    val bestResult = if (angleScanResults.isNotEmpty()) {
                                        angleScanResults.maxByOrNull { it.confidence }!!
                                    } else {
                                        AngleScanResult(
                                            fruit = detectedFruit,
                                            ripeness = detectedRipeness,
                                            confidence = detectedConfidence.value,
                                            isNatural = isNaturalRipening.value,
                                            imagePath = capturedImagePath.value
                                        )
                                    }

                                    onSaveFruit(
                                        bestResult.fruit,
                                        bestResult.ripeness,
                                        bestResult.isNatural,
                                        bestResult.confidence,
                                        bestResult.imagePath
                                    )
                                    isSaved.value = true
                                    showScanAgainDialog.value = true
                                }
                            },
                            onCancel = {
                                handleCancel() },
                            onScanOtherAngle = handleScanAnotherAngle,
                            showScanOtherAngle = !fromGallery.value,
                            anglesScanned = angleScansCount.value,

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
        } else if (showNoFruitDetected.value) {
            Text(
                "No fruit detected",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (showDifferentFruitMessage.value) {
            Text(
                "A different fruit is scanned",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
            LaunchedEffect(Unit) {
                delay(1500)
                showDifferentFruitMessage.value = false
                // Don't auto-resume - let user click scan button again
            }
        }
    }

    // 🔁 Scan again dialog
    if (showScanAgainDialog.value) {
        ScanAgain(
            onYes = {
                detectedState.value = false
                detectedFruitState.value = ""
                detectedRipenessState.value = ""
                detectedConfidence.value = 0f
                isSaved.value = false
                isBottomSheetVisible.value = false
                isScanning.value = false
                showScanAgainDialog.value = false

                // Reset angle scanning states
                isScanningAnotherAngle.value = false
                originalScannedFruit.value = null
                angleScansCount.value = 0
                angleScanResults.clear()
                showDifferentFruitMessage.value = false
            },
            onNo = {
                cameraRef.value?.cameraControl?.enableTorch(false)
                flashEnabled.value = false
                showScanAgainDialog.value = false
                onHome()
            },
            isDarkMode = isDarkMode
        )
    }

    // 🧾 How to use overlay
    if (showHowTo.value) {
        HowToOverlay(onDismiss = { showHowTo.value = false })
    }

    // 🆕 Go to gallery dialog
    if (showGoToGalleryDialog.value) {
        GoToGallery(
            isDarkMode = isDarkMode,
            onYes = {
                showGoToGalleryDialog.value = false

                // Stop any ongoing scanning
                isScanning.value = false
                detectedState.value = false
                isBottomSheetVisible.value = false

                // Reset angle scanning state
                isScanningAnotherAngle.value = false
                originalScannedFruit.value = null
                angleScansCount.value = 0
                angleScanResults.clear()
                showDifferentFruitMessage.value = false

                // Reset captured image path (optional, avoids stale path)
                capturedImagePath.value = null

                // Now launch gallery safely
                galleryLauncher.launch("image/*")
            },

            onNo = {
                showGoToGalleryDialog.value = false
            }
        )
    }
}