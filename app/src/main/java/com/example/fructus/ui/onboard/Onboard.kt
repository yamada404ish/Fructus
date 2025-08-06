package com.example.fructus.ui.onboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.util.DataStoreManager

// Composable function that represents the OnboardingScreen
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit // Callback function triggered when user taps "Get Started"
) {
    // Get the current Android context (needed for DataStore)
    val context = LocalContext.current

    // Create a single instance of DataStoreManager and remember it across recompositions
    val dataStore = remember { DataStoreManager(context) }

    // Create the OnboardingViewModel using the custom factory to inject DataStore
    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(dataStore)
    )

    // Show the actual UI content of the onboarding screen
    OnboardingScreenContent(
        viewModel = viewModel,           // Pass the ViewModel to the content composable
        onGetStarted = onGetStarted      // Pass the callback to trigger when onboarding finishes
    )
}
