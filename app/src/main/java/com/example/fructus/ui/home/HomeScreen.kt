package com.example.fructus.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fructus.data.local.FruitDatabase
import com.example.fructus.navigation.Notification
import com.example.fructus.ui.shared.RequestNotificationPermission
import com.example.fructus.util.DataStoreManager
import com.example.fructus.util.isNotificationPermissionGranted

@Composable
fun HomeScreen(
    navController: NavController, // Used to navigate between screens
    onFruitClick: (Int) -> Unit// Callback function to handle fruit click event
) {
    val context = LocalContext.current
    val db = remember { FruitDatabase.getDatabase(context) }
    val dataStore = remember { DataStoreManager(context) }

    // Creates HomeViewModel with a custom factory passing in the FruitDao
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(db.fruitDao())
    )

    val state by viewModel.state.collectAsState()
    val shouldRequestPermission by dataStore.shouldRequestNotificationFlow.collectAsState(initial = false)
    val showDialog = remember { mutableStateOf(false) }


    // Runs when shouldRequestPermission changes
    LaunchedEffect(shouldRequestPermission) {
        // If DataStore says we should request permission and it's not granted yet
        if (shouldRequestPermission && !isNotificationPermissionGranted(context)) {
            showDialog.value = true
            dataStore.setRequestNotificationPermission(false)
        }
    }

    // If dialog state is true, show the permission request dialog
    if (showDialog.value) {
        RequestNotificationPermission(
            onGranted = { showDialog.value = false },
            onDenied = { showDialog.value = false }
        )
    }

    HomeScreenContent(
        state = state,
        onFruitClick = onFruitClick,
        onNotificationClick = { navController.navigate(Notification) }
    )
}


/*
Initializes the DB and ViewModel

Checks if notification permission should be requested (based on DataStore)

If yes and not yet granted, shows a permission dialog (only once!)

Displays the home screen with fruit list and notification icon
*/
