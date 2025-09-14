package com.example.fructus.ui.setting.components

import androidx.compose.runtime.Composable
import com.example.fructus.ui.onboard.OnboardingScreenContent

@Composable
fun OnboardingPreviewScreen(
    onGetStarted: () -> Unit
) {
    // Don't create ViewModel here - just show the UI
    OnboardingScreenContent(
        viewModel = null, // We'll modify OnboardingScreenContent to handle null viewModel
        onGetStarted = onGetStarted
    )
}