package com.example.fructus.ui.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.fructus.R
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily



@Composable
fun FructusGuideStep(
    iconRes: Painter,
    title: String,
    description: String
) {
    val colors = MaterialTheme.appColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = iconRes,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFontFamily,
                color = colors.textPrimary
            )
            Text(
                text = description,
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily,
                color = colors.textSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun FructusOnboardingOverlay(
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val colors = MaterialTheme.appColors

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Animation states
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400)
    )

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400, easing = EaseOutBounce)
    )

    BackHandler(enabled = true) {
        isVisible = false
        onDismiss()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f)
            .alpha(animatedAlpha)
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(
                indication = null, // No ripple
                interactionSource = remember { MutableInteractionSource() }
            ) {}

    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(200, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(500, delayMillis = 100)),
            exit = fadeOut(animationSpec = tween(300)) +
                    scaleOut(animationSpec = tween(300))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .scale(animatedScale)
                    .align(Alignment.Center)
                    .clickable { },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "How to use",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        color = colors.textPrimary, // Green theme
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Scan and know about your fruits!",
                        fontSize = 16.sp,
                        fontFamily = poppinsFontFamily,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    FructusGuideStep(
                        painterResource(R.drawable.scan),
                        title = "Scan Your Fruits",
                        description = "Tap the scan button below to add new fruits"
                    )

                    FructusGuideStep(
                        iconRes = painterResource(R.drawable.guide_filter),
                        title = "Filter & Sort",
                        description = "Use the filter on the top right to toggle to or sort by newest/oldest"
                    )

                    FructusGuideStep(
                        painterResource(R.drawable.guide_notif),
                        title = "Access Notifications",
                        description = "Tap the notification icon to see your notifications"
                    )

                    FructusGuideStep(
                        painterResource(R.drawable.guide_setting),
                        title = "Settings",
                        description = "Access settings to personalize your fruit tracking experience"
                    )

                    FructusGuideStep(
                        painterResource(R.drawable.info),
                        title = "Ripeness Guide",
                        description = "See how the fruit changes from unripe to spoiled, along with its corresponding shelf life."
                    )


                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.button
                            )
                        ) {
                            Text(
                                "Got It!",
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}


