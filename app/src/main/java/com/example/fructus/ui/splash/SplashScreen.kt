package com.example.fructus.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fructus.R
import com.example.fructus.ui.onboard.OnboardingViewModel
import com.example.fructus.ui.onboard.OnboardingViewModelFactory
import com.example.fructus.util.DataStoreManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    // Callback triggered after splash animation finishes
    onAnimationFinished: (onboardingCompleted: Boolean) -> Unit
) {
    // Load the Lottie splash animation from raw resources
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fructus_splash))

    // Controls how the Lottie animation is played
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1, // Play only once
        speed = 1.2f,   // Slightly faster playback
        clipSpec = LottieClipSpec.Progress(0f, 1f), // Play from start to end
        isPlaying = true,
        restartOnPlay = false
    )

    // Get application context and initialize DataStore
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    // ViewModel that reads onboarding completion state
    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(dataStore)
    )

    // Listen to onboarding completion status from DataStore
    val onboardingCompleted by viewModel.isOnboardingCompleted.collectAsState(initial = false)

    // Used to ensure navigation only happens once
    var hasNavigated by remember { mutableStateOf(false) }

    // Wait until animation completes, then call the navigation callback
    LaunchedEffect(progress) {
        if (progress == 1f && !hasNavigated) {
            hasNavigated = true
            delay(300) // Small delay after animation
            onAnimationFinished(onboardingCompleted)
        }
    }

    // UI: Fullscreen centered animation with app background color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // Play the splash animation
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(450.dp) // Adjust animation size
        )
    }
}


/*
Plays a Lottie animation on launch.

Checks if the onboarding is completed using DataStore.

After animation finishes, navigates based on onboarding status.
*/