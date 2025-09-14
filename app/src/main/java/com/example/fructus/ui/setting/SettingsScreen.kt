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
    onNavigateToOnboardingPreview: () -> Unit,
    onNavigateToFreshStart: () -> Unit,
) {
    val context = LocalContext.current
    val db = FruitDatabase.getDatabase(context)

    val viewModel = remember {
        SettingsViewModel(
            context = context,
            dataStore = DataStoreManager(context),
            notificationDao = db.notificationDao(),
            fruitDao = db.fruitDao()
        )
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateToOnboardingPreview) {
        if (state.navigateToOnboardingPreview) {
            onNavigateToOnboardingPreview()
            viewModel.resetPreviewNavigateFlag()
        }
    }

    LaunchedEffect(state.navigateToFreshStart) {
        if (state.navigateToFreshStart) {
            onNavigateToFreshStart()
            viewModel.resetFreshStartNavigateFlag()
        }
    }

    SettingsScreenContent(
        state = state,
        onNavigateUp = onNavigateUp,
        onToggleNotifications = viewModel::onToggleNotifications,
        onToggleDarkMode = viewModel::onToggleDarkMode, // Pass dark mode handler
        onEnableNotifications = {
            navigateToNotificationSettings(context)
            viewModel.markReturnedFromSettings()
        },
        onDismissSheet = viewModel::hideBottomSheet,
        onShowClearDialog = viewModel::showClearDialog,
        onClearAll = viewModel::clearAllData,
        onDismissClearDialog = viewModel::hideClearDialog,
        onShowOnboarding = viewModel::showOnboarding
    )
}