package com.example.fructus.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fructus.data.local.FruitDatabase
import com.example.fructus.navigation.Notification
import com.example.fructus.ui.shared.CameraPermissionModal
import com.example.fructus.util.DataStoreManager
import com.example.fructus.util.SequentialPermissionRequest
import com.example.fructus.util.isCameraPermissionGranted

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    navController: NavController,
    onFruitClick: (Int) -> Unit,
    onNavigateToScan: () -> Unit,
) {
    val context = LocalContext.current
    val db = remember { FruitDatabase.getDatabase(context) }
    val dataStore = remember { DataStoreManager(context) }

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(db.fruitDao())
    )

    val state by viewModel.state.collectAsState()
    val shouldRequestPermission by dataStore.shouldRequestNotificationFlow.collectAsState(initial = false)

    var showInitialPermissions by remember { mutableStateOf(false) }
    var showCameraPermissionModal by remember { mutableStateOf(false) }

    // Check for initial permissions on first launch
    LaunchedEffect(shouldRequestPermission) {
        if (shouldRequestPermission) {
            showInitialPermissions = true
            dataStore.setRequestNotificationPermission(false)
        }
    }

    // Show initial permission flow (notification + camera)
    if (showInitialPermissions) {
        SequentialPermissionRequest(
            context = context,
            onAllPermissionsGranted = {
                showInitialPermissions = false
            },
            onPermissionsDenied = {
                showInitialPermissions = false
            }
        )
    }

    // Show camera permission modal
    if (showCameraPermissionModal) {
        CameraPermissionModal(
            onDismiss = {
                showCameraPermissionModal = false
            }
        )
    }

    HomeScreenContent(
        state = state,
        onFruitClick = onFruitClick,
        onNotificationClick = { navController.navigate(Notification) },
        onScanClick = {
            if (isCameraPermissionGranted(context)) {
                // Camera permission granted, navigate to scan
                onNavigateToScan()
            } else {
                // Camera permission not granted, show modal
                showCameraPermissionModal = true
            }
        }
    )
}


/*
Initializes the DB and ViewModel

Checks if notification permission should be requested (based on DataStore)

If yes and not yet granted, shows a permission dialog (only once!)

Displays the home screen with fruit list and notification icon
*/