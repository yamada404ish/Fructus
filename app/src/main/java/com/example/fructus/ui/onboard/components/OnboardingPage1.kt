package com.example.fructus.ui.onboard.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun OnboardingWelcomePage() {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Image
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                    animationSpec = tween(800),
                    initialScale = 0.8f
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon1),
                    contentDescription = "",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 32.dp)
                )
            }

            //Title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 200),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Welcome to Fructus!",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )            }

            // Description
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 400),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "An app that can help you track and find out the shelf life of your fruits!",
                    fontFamily = poppinsFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}