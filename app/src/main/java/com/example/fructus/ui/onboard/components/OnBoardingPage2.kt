package com.example.fructus.ui.onboard.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.onboard.OnboardingScreenContent
import com.example.fructus.ui.onboard.OnboardingViewModel
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.DataStoreManager


@Composable
fun OnboardingPage2() {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.onbg2),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Welcome text in upper left
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 40.dp), // Adjust padding as needed
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                animationSpec = tween(600, delayMillis = 200),
                initialOffsetY = { -it / 4 } // Slide from top instead of bottom
            )
        ) {
            Column(modifier = Modifier
                .padding(bottom = 14.dp)) {
                Text(
                    "Identify your",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )

            }

        }
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 40.dp), // Adjust padding as needed
            enter = fadeIn(animationSpec = tween(800, delayMillis = 400)) + slideInVertically(
                animationSpec = tween(600, delayMillis = 200),
                initialOffsetY = { -it / 4 } // Slide from top instead of bottom
            )
        ) {
            Column(modifier = Modifier
                .padding(top = 24.dp)) {
                Text(
                    "Fruits",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

            }

        }

        // Description text above carousel (bottom area)
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .padding(horizontal = 40.dp), // Adjust bottom padding based on your carousel height
            enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + slideInVertically(
                animationSpec = tween(600, delayMillis = 400),
                initialOffsetY = { it / 3 }
            )
        ) {
            Text(
                "Know your fruits and keep track of them befor they spoil!",
                fontFamily = poppinsFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
private fun OnBoardingPage2(){
    FructusTheme {
        OnboardingScreenContent(
            viewModel = OnboardingViewModel(DataStoreManager(context = LocalContext.current)),
            onGetStarted = {},
            onStarted = {}
        )
    }

}