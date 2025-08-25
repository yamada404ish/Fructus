package com.example.fructus.ui.onboard

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.ui.onboard.components.OnboardingPage2
import com.example.fructus.ui.onboard.components.OnboardingPage3
import com.example.fructus.ui.onboard.components.OnboardingPage4
import com.example.fructus.ui.onboard.components.OnboardingPage5
import com.example.fructus.ui.onboard.components.OnboardingWelcomePage
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.DataStoreManager
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreenContent(
    viewModel: OnboardingViewModel,
    onGetStarted: () -> Unit,
//    onStarted: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF0EFE9))) {
        Column(modifier = Modifier.fillMaxSize()) {



            // Carousel pages 0-3
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(600)),
                exit = fadeOut(tween(400)),
                modifier = Modifier.weight(1f)
            ) {
                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> OnboardingWelcomePage()
                        1 -> OnboardingPage2()
                        2 -> OnboardingPage3()
                    }
                }
            }




            // Dot Indicators

                Column(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {


                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        repeat(3) { index ->
                            val isSelected = pagerState.currentPage == index
                            val dotWidth by animateDpAsState(
                                targetValue = if (isSelected) 24.dp else 8.dp, // Long vs small
                                animationSpec = spring(
                                    Spring.DampingRatioMediumBouncy,
                                    Spring.StiffnessLow
                                ),
                                label = "dot_width"
                            )

                            Box(
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .padding(horizontal = 4.dp)
                                    .width(dotWidth)
                                    .height(8.dp) // Fixed height
                                    .background(
                                        color = if (isSelected) Color(0xFFBADBA2) else Color(0xFFD1CEBA),
                                        shape = RoundedCornerShape(3.dp) // Half of height for pill shape
                                    )
                            )
                        }

                    }



                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.completeOnboarding()
                                    viewModel.setRequestNotificationOnce()
                                    onGetStarted()
                                }
                            },
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .height(50.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBADBA2), // background
                                contentColor = Color.Black
                            )// text/icon
                        )
                        {
                            Text("Get Started")
                        }


                }
            }
        }



    }



/*@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
private fun OnboardingScreenContent() {
    FructusTheme {
        OnboardingScreenContent(
            viewModel = OnboardingViewModel(DataStoreManager(context = LocalContext.current)),
            onStarted = {},
            onGetStarted = {}
        )
    }
}*/
