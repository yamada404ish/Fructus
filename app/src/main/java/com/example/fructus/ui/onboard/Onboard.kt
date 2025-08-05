package com.example.fructus.ui.onboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.onboard.components.OnboardingPage2
import com.example.fructus.ui.onboard.components.OnboardingPage3
import com.example.fructus.ui.onboard.components.OnboardingPage4
import com.example.fructus.ui.onboard.components.OnboardingPage5
import com.example.fructus.ui.onboard.components.OnboardingWelcomePage
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.DataStoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingCarousel(
    onGetStarted: () -> Unit = {},
    viewModel: OnboardingViewModel

) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()


    val isLastPage by remember { derivedStateOf { pagerState.currentPage == 4 } }


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
                        0 -> OnboardingWelcomePage()
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
                        scope.launch {
                            viewModel.completeOnboarding()
                            dataStore.setRequestNotificationPermission(true)
                            onGetStarted()
                        }
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
                        }
                    }
                }
            }
        }
        // Skip button
        AnimatedVisibility(
            visible = !isLastPage,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Skip",
                fontFamily = poppinsFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = 4,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Splash1() {
    FructusTheme {
//        OnboardingCarousel()
    }
}



