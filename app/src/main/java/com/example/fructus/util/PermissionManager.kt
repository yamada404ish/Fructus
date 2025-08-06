package com.example.fructus.util

import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class PermissionState {
    IDLE,
    REQUESTING_NOTIFICATION,
    REQUESTING_CAMERA,
    COMPLETED
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SequentialPermissionRequest(
    context: Context,
    onAllPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    var permissionState by remember { mutableStateOf(PermissionState.IDLE) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = if (isGranted) {
            // Notification granted or not needed, move to camera permission
            PermissionState.REQUESTING_CAMERA
        } else {
            // Notification denied, still proceed to camera
            PermissionState.REQUESTING_CAMERA
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = PermissionState.COMPLETED
        if (isGranted) {
            onAllPermissionsGranted()
        } else {
            onPermissionsDenied()
        }
    }

    LaunchedEffect(permissionState) {
        when (permissionState) {
            PermissionState.IDLE -> {
                // Check if we need to request notification permission
                permissionState = if (!isNotificationPermissionGranted(context)) {
                    PermissionState.REQUESTING_NOTIFICATION
                } else {
                    // Skip notification, go to camera
                    PermissionState.REQUESTING_CAMERA
                }
            }
            PermissionState.REQUESTING_NOTIFICATION -> {
                notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            PermissionState.REQUESTING_CAMERA -> {
                if (!isCameraPermissionGranted(context)) {
                    cameraLauncher.launch(android.Manifest.permission.CAMERA)
                } else {
                    // Camera already granted
                    permissionState = PermissionState.COMPLETED
                    onAllPermissionsGranted()
                }
            }
            PermissionState.COMPLETED -> {
                // Do nothing, flow is complete
            }
        }
    }
}