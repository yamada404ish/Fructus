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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.onboard.components.OnboardingPage2
import com.example.fructus.ui.onboard.components.OnboardingPage3
import com.example.fructus.ui.onboard.components.OnboardingPage4
import com.example.fructus.ui.onboard.components.OnboardingPage5
import com.example.fructus.ui.onboard.components.OnboardingWelcomePage
import com.example.fructus.ui.theme.poppinsFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreenContent(
    viewModel: OnboardingViewModel,
    onGetStarted: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val isLastPage by remember { derivedStateOf { pagerState.currentPage == 4 } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Carousel pages 0-3
            AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn(tween(600)),
                exit = fadeOut(tween(400)),
                modifier = Modifier.weight(1f)
            ) {
                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> OnboardingWelcomePage()
                        1 -> OnboardingPage2()
                        2 -> OnboardingPage3()
                        3 -> OnboardingPage4()
                    }
                }
            }

            // Page 4 (Get Started)
            AnimatedVisibility(
                visible = isLastPage,
                enter = fadeIn(tween(700, delayMillis = 300)) +
                        slideInVertically(tween(700, delayMillis = 300)) { it / 3 },
                exit = fadeOut(tween(300)),
                modifier = Modifier.weight(1f)
            ) {
                OnboardingPage5(
                    onGetStarted = {
                        scope.launch {
                            viewModel.completeOnboarding()
                            viewModel.setRequestNotificationOnce()
                            onGetStarted()
                        }
                    }
                )
            }

            // Dot Indicators
            AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300))
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotSize by animateDpAsState(
                            targetValue = if (isSelected) 16.dp else 10.dp,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                            label = "dot_size"
                        )
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                        )
                        if (index < 4) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }

        // Skip button
        AnimatedVisibility(
            visible = !isLastPage,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 20.dp)
        ) {
            Text(
                text = "Skip",
                fontFamily = poppinsFontFamily,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    scope.launch {
                        pagerState.animateScrollToPage(4, animationSpec = tween(500, easing = FastOutSlowInEasing))
                    }
                }
            )
        }
    }
}
