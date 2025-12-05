package com.example.fructus.ui.onboard.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.responsiveSp

@Composable
fun OnboardingWelcomePage(
    isDarkMode: Boolean
) {

    val colors = MaterialTheme.appColors
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val backgroundImage = if (isDarkMode) {
        R.drawable.dm_onboard1 // Dark mode image
    } else {
        R.drawable.lm_onboard1 // Light mode image
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier
                .align(Alignment.TopStart), // Adjust padding as needed
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            // Background Image
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

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
                    "Welcome to",
                    fontFamily = poppinsFontFamily,
                    color = colors.textPrimary,
                    fontSize = responsiveSp(12,14,16),
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
                .padding(top = 14.dp)) {
                Text(
                    "Fructus",
                    fontFamily = poppinsFontFamily,
                    color = colors.textPrimary,
                    fontSize = responsiveSp(30,50,60),
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
                "An application that helps you find out the shelf life of your fruits!",
                fontFamily = poppinsFontFamily,
                textAlign = TextAlign.Center,
                fontSize = responsiveSp(14,16,18),
                fontWeight = FontWeight.Normal,
                color = colors.textPrimary,
            )
        }

    }
}


