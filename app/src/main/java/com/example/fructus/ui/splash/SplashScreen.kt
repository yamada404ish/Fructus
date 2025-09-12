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

@Composable
fun SplashScreen(
    onAnimationFinished: (onboardingCompleted: Boolean) -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fructus_splash))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.2f, // Always normal speedZ
        clipSpec = LottieClipSpec.Progress(0f, 1f),
        isPlaying = true,
        restartOnPlay = false
    )

    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(dataStore)
    )
    val onboardingCompleted by viewModel.isOnboardingCompleted.collectAsState(initial = null)

    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(progress, onboardingCompleted) {
        if (progress == 1f && !hasNavigated && onboardingCompleted != null) {
            hasNavigated = true
            onAnimationFinished(onboardingCompleted == true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        composition?.let {
            LottieAnimation(
                composition = it,
                progress = { progress },
                modifier = Modifier.size(450.dp)
            )
        }
    }
}