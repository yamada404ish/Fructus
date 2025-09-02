package com.example.fructus.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.fructus.util.DataStoreManager

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current

    // Create ViewModel with context and datastore
    val viewModel = remember {
        SettingsViewModel(
            context = context,
            dataStore = DataStoreManager(context)
        )
    }

    // Observe the UI state
    val state by viewModel.state.collectAsState()

    // Pass the state and handlers to UI content
    SettingsScreenContent(
        state = state,
        onNavigateUp = onNavigateUp,
        onToggleNotifications = viewModel::onToggleNotifications,
//        onEnableNotifications = {
//            // Open system notification settings
//            navigateToNotificationSettings(context)
//            viewModel.markReturnedFromSettings()
//        },
//        onDismissSheet = viewModel::hideBottomSheet,
//        onShowClearDialog = viewModel::showClearDialog,
//        onClearAll = viewModel::hideClearDialog, // Replace with your real logic if needed
//        onDismissClearDialog = viewModel::hideClearDialog
    )
}
