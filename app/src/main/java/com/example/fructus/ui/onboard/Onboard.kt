package com.example.fructus.ui.onboard

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.fructus.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import com.example.fructus.ui.theme.FructusTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.fructus.ui.theme.poppinsFontFamily
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.material3.TextButton
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FructusTheme {
                OnboardingCarousel()
            }
        }    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingCarousel() {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()


    val isLastPage = pagerState.currentPage == 4

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // carousel
            AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut(animationSpec = tween(400)),
                modifier = Modifier.weight(1f)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> OnboardingPage1()
                        1 -> OnboardingPage2()
                        2 -> OnboardingPage3()
                        3 -> OnboardingPage4()
                        4 -> {
                            // This won't be visible due to AnimatedVisibility above
                        }
                    }                }
            }
            // Get Started page
            AnimatedVisibility(
                visible = isLastPage,
                enter = fadeIn(animationSpec = tween(700, delayMillis = 300)) + slideInVertically(
                    animationSpec = tween(700, delayMillis = 300),
                    initialOffsetY = { it / 3 }
                ),
                exit = fadeOut(animationSpec = tween(300)),
                modifier = Modifier.weight(1f)
            ) {
                OnboardingPage5(
                    onGetStarted = {
                        // Add navigation to main app here
                    }
                )
            }

            // Page indicator
            AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index -> // dots
                        val isSelected = pagerState.currentPage == index

                        val dotSize by animateDpAsState(
                            targetValue = if (isSelected) 16.dp else 10.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "dot_size"
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                        )
                        if (index < 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }                    }
                }            }        }
        // Skip button
        AnimatedVisibility(
            visible = !isLastPage,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            TextButton(
                onClick = {
                    scope.launch {

                        pagerState.animateScrollToPage(
                            page = 4,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        )                    }
                }            ) {
                Text(
                    "Skip",
                    fontFamily = poppinsFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }    }}

// Page 1
@Composable
fun OnboardingPage1() {
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
                )            }

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
                )            }
        }    }}

// Page 2
@Composable
fun OnboardingPage2() {
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
                    painter = painterResource(id = R.drawable.onb2),
                    contentDescription = "",
                    modifier = Modifier
                        .size(320.dp)
                        .padding(bottom = 32.dp)
                )            }

            //Title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 200),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Track Your Fruits",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )            }

            //Description
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 400),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Keep track of all your fruits and never let them spoil again!",
                    fontFamily = poppinsFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                )            }
        }    }}

// Page 3
@Composable
fun OnboardingPage3() {
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
            // image
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                    animationSpec = tween(800),
                    initialScale = 0.8f
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cam),
                    contentDescription = "",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 32.dp)
                )            }

            //Title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 200),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Know your fruits",
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
                    "Get detailed information about how long your fruits will stay fresh through camera detection!",
                    fontFamily = poppinsFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                )            }
        }    }}

// Page 4
@Composable
fun OnboardingPage4() {
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
            // Image
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                    animationSpec = tween(1300),
                    initialScale = 0.8f
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notif),
                    contentDescription = "",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 32.dp)
                )            }


            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(700, delayMillis = 300)) + slideInVertically(
                    animationSpec = tween(700, delayMillis = 300),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Smart Notifications",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )            }

            // description
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(700, delayMillis = 600)) + slideInVertically(
                    animationSpec = tween(700, delayMillis = 600),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Get reminded before your fruits expire so you can enjoy them at their best!",
                    fontFamily = poppinsFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                )            }
        }    }}

// Page 5
@Composable
fun OnboardingPage5(onGetStarted: () -> Unit) {
    var contentVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contentVisible = true
        kotlinx.coroutines.delay(500)
        buttonVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Image
            AnimatedVisibility(
                visible = contentVisible,
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
                )            }

            //Title
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 200),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "Ready to Start!",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )            }

            //Description
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 400),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Text(
                    "You're all set! Start tracking your fruits and enjoy fresh produce every day!",
                    fontFamily = poppinsFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                )            }
        }
        // Get Started
        AnimatedVisibility(
            visible = buttonVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { it / 2 }
            ) + scaleIn(
                animationSpec = tween(600),
                initialScale = 0.8f
            )
        ) {
            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
            ) {
                Text(
                    "Get Started",
                    fontSize = 18.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold
                )

            }
        }    }}

@Preview(showBackground = true)
@Composable
private fun Splash1() {
    FructusTheme {
        OnboardingCarousel()
    }
}



