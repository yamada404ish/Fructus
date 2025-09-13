package com.example.fructus.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.fructus.data.local.FruitDatabase
import com.example.fructus.util.DataStoreManager
import com.example.fructus.util.navigateToNotificationSettings

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
) {
    val context = LocalContext.current
    val db = FruitDatabase.getDatabase(context)

    // Create ViewModel with context and datastore
    val viewModel = remember {
        SettingsViewModel(
            context = context,
            dataStore = DataStoreManager(context),
            notificationDao = db.notificationDao(),
            fruitDao = db.fruitDao()

        )
    }

    // Observe the UI state
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateToOnboarding) {
        if (state.navigateToOnboarding) {
            onNavigateToOnboarding()
            viewModel.resetNavigateFlag() // 🔥 prevents multiple triggers
        }
    }

    // Pass the state and handlers to UI content
    SettingsScreenContent(
        state = state,
        onNavigateUp = onNavigateUp,
        onToggleNotifications = viewModel::onToggleNotifications,
        onEnableNotifications = {
            // Open system notification settings
            navigateToNotificationSettings(context)
            viewModel.markReturnedFromSettings()
        },
        onDismissSheet = viewModel::hideBottomSheet,
        onShowClearDialog = viewModel::showClearDialog,
        onClearAll = viewModel::clearAllData, // Replace with your real logic if needed
        onDismissClearDialog = viewModel::hideClearDialog
    )
}
