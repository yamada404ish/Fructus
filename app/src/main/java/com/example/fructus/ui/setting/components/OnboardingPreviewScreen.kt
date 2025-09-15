package com.example.fructus.ui.setting.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.fructus.ui.onboard.OnboardingScreenContent
import com.example.fructus.util.DataStoreManager

@Composable
fun OnboardingPreviewScreen(
    onGetStarted: () -> Unit,
) {

    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val isDarkMode by dataStore.darkModeFlow.collectAsState(initial = false)

    // Don't create ViewModel here - just show the UI
    OnboardingScreenContent(
        viewModel = null, // We'll modify OnboardingScreenContent to handle null viewModel
        onGetStarted = onGetStarted,
        isDarkMode = isDarkMode // Set to true for dark mode preview
    )
}