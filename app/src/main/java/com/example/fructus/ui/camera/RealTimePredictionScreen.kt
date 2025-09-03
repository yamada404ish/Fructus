package com.example.fructus.ui.camera

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun Camera(
    onNavigateUp: () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionState = remember { mutableStateOf(false) }

    val detected = remember { mutableStateOf(false) }
    val detectedFruit = remember { mutableStateOf("") }
    val detectedRipeness = remember { mutableStateOf("") }

    val db = remember { com.example.fructus.data.local.FruitDatabase.getDatabase(context) }
    val cameraViewModel: CameraViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CameraViewModelFactory(
            db.fruitDao(),
            db.notificationDao())
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            permissionState.value = granted
            if (!granted) {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    )

    // Request camera permission
    LaunchedEffect(Unit) {
        when (android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) -> {
                permissionState.value = true
            }
            else -> launcher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if (!permissionState.value) {
        return
    }

    CameraScreenContent(
        detected = detected.value,
        detectedFruit = detectedFruit.value,
        detectedRipeness = detectedRipeness.value,
        lifecycleOwner = lifecycleOwner,
        detectedState = detected,
        detectedFruitState = detectedFruit,
        detectedRipenessState = detectedRipeness,
        onNavigateUp = onNavigateUp,
        onSaveFruit = { fruit, ripeness, process, confidence ->
            cameraViewModel.saveFruit(fruit, ripeness, process, confidence )
        }
    )
}
