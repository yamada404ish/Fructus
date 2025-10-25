package com.example.fructus.ui.camera

import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.fructus.ui.camera.components.HowToOverlay
import com.example.fructus.ui.camera.components.ScanAgain
import com.example.fructus.ui.shared.CustomBottomSheet
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.classifyFruit
import com.example.fructus.util.classifyRipeness
import com.example.fructus.util.cropToScanBox
import com.example.fructus.util.formatShelfLifeRange
import com.example.fructus.util.getShelfLifeRange
import com.example.fructus.util.rotate
import com.example.fructus.util.saveBitmapToInternalStorage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
    detected: Boolean,
    detectedFruit: String,
    detectedRipeness: String,

    dtProcess: Boolean = true,

    lifecycleOwner: LifecycleOwner,
    detectedState: MutableState<Boolean>,
    detectedFruitState: MutableState<String>,
    detectedRipenessState: MutableState<String>,
    onSaveFruit: (String, String, Boolean, Float, String?) -> Unit,
    onNavigateUp: () -> Unit,
    isDarkMode: Boolean,
    onHome: () -> Unit
) {
    val detectedConfidence = remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val isSaved = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }
    val isScanning = remember { mutableStateOf(false) }
    val isBottomSheetVisible = remember { mutableStateOf(false) }
    val capturedImagePath = remember { mutableStateOf<String?>(null) }

    val showNoFruitDetected = remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            bitmap?.let { selectedImage ->
                isScanning.value = true
                showNoFruitDetected.value = false

                try {
                    val fileName = "fruit_gallery_${System.currentTimeMillis()}"
                    val imagePath = saveBitmapToInternalStorage(context, selectedImage, fileName)
                    capturedImagePath.value = imagePath

                    val fruitResult = classifyFruit(selectedImage, context)
                    val ripenessResult = classifyRipeness(fruitResult.label, selectedImage, context)

                    detectedFruitState.value = fruitResult.label
                    detectedRipenessState.value = ripenessResult.label
                    detectedConfidence.value = ripenessResult.confidence
                    detectedState.value = true

                    if (fruitResult.label != "No fruit detected" && ripenessResult.label != "Unknown") {
                        isBottomSheetVisible.value = true
                    } else {
                        // Show centered text instead of toast
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

    // Shelf life info
    val shelfLifeRange = getShelfLifeRange(detectedFruit, detectedRipeness)
    val shelfLifeDisplay =
        if (shelfLifeRange.minDays == -1) "---" else formatShelfLifeRange(shelfLifeRange)

    val showScanAgainDialog = remember { mutableStateOf(false) }
    val showHowTo = remember { mutableStateOf(false) }
    val colors = MaterialTheme.appColors

    val handleCancel: () -> Unit = {
        isBottomSheetVisible.value = false
        detectedState.value = false
        isSaved.value = false
    }

    BackHandler {
        when {
            showHowTo.value -> showHowTo.value = false
            isBottomSheetVisible.value -> {
                isBottomSheetVisible.value = false
                detectedState.value = false
                isSaved.value = false
            }
            else -> {
                cameraRef.value?.cameraControl?.enableTorch(false)
                flashEnabled.value = false
                onNavigateUp()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 📷 CAMERA PREVIEW
        AndroidView(
            factory = {
                val previewView = PreviewView(it)

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224, 224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->
                            if (isScanning.value && !detectedState.value) {
                                val bitmap = imageProxy.toBitmap() ?: run {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                val rotatedBitmap =
                                    bitmap.rotate(imageProxy.imageInfo.rotationDegrees)
                                try {
                                    val croppedForBox = rotatedBitmap.cropToScanBox(it)
                                    val fruitResult = classifyFruit(croppedForBox, it)
                                    val ripenessResult =
                                        classifyRipeness(fruitResult.label, croppedForBox, it)

                                    isSaved.value = false

                                    val fileName = "fruit_${System.currentTimeMillis()}"
                                    val imagePath = saveBitmapToInternalStorage(
                                        it,
                                        croppedForBox,
                                        fileName
                                    )
                                    capturedImagePath.value = imagePath

                                    detectedFruitState.value = fruitResult.label
                                    detectedRipenessState.value = ripenessResult.label
                                    detectedConfidence.value = ripenessResult.confidence
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
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            enabled = !showHowTo.value,
                            onClick = {
                                cameraRef.value?.cameraControl?.enableTorch(false)
                                flashEnabled.value = false
                                onNavigateUp()
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!isBottomSheetVisible.value) {
                Icon(
                    painter = painterResource(
                        if (flashEnabled.value) R.drawable.flash_on_button else R.drawable.flash_off_button
                    ),
                    contentDescription = "Flashlight",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            enabled = !showHowTo.value,
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
                        .clickable(
                            enabled = !showHowTo.value,
                            onClick = { galleryLauncher.launch("image/*") },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(R.drawable.camera_scan_icon),
                    contentDescription = "camera icon",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable(
                            enabled = !showHowTo.value,
                            onClick = { isScanning.value = true },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.size(20.dp))

                Icon(
                    painter = painterResource(R.drawable.howtouse),
                    contentDescription = "Gallery",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            enabled = !showHowTo.value,
                            onClick = { showHowTo.value = true },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
            }
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
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    LaunchedEffect(detectedFruit) {
                        delay(800)
                        detectedState.value = false
                        isScanning.value = false
                        showNoFruitDetected.value = false
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
                            ripeningProcess = dtProcess,
                            confidence = detectedConfidence.value,
                            shelfLifeRange = shelfLifeRange,
                            shelfLifeDisplay = shelfLifeDisplay,
                            isSaved = isSaved.value,
                            onSave = {
                                if (!isSaved.value) {
                                    onSaveFruit(
                                        detectedFruit,
                                        detectedRipeness,
                                        dtProcess,
                                        detectedConfidence.value,
                                        capturedImagePath.value
                                    )
                                    isSaved.value = true
                                    showScanAgainDialog.value = true
                                }
                            },
                            onCancel = handleCancel
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
        }
    }

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
            },
            onNo = {
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

