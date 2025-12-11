package com.example.fructus.ui.setting.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.responsiveDp
import com.example.fructus.util.responsiveSp

@Composable
fun Disclaimer(
    modifier: Modifier = Modifier

) {

    val colors = MaterialTheme.appColors
    val teamSections = listOf(
        "Backend" to listOf("Adrian Miclat | Jenevieve Tonion"),
        "Frontend" to listOf("Jenevieve Tonion | Hannah Quinto"),
        "Resources" to listOf("Missy Tanhueco | Michael Sazon", "Stephen Ortega")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card (
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
                .clickable (
                    onClick = { },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.bg
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                Text(
                    "Detection Disclaimer",
                    fontFamily = poppinsFontFamily,
                    fontSize = responsiveSp(22,26,18),
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(responsiveDp(10,18,20)))

                // Loop through each section

                    Text(
                        text = "Fructus uses AI to detect fruits and their ripeness. Results are estimates, not guarantees.",
                        fontFamily = poppinsFontFamily,
                        fontSize = responsiveSp(11,15,18),
                        fontWeight = FontWeight.Normal,
                        color = colors.textPrimary,
//                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)

                    )

                    Spacer(modifier = Modifier.height(14.dp))

                val bulletPoints = listOf(
                    "Some fruits may stay green or have colors that confuse the scanner.",
                    "Objects with similar shapes or colors can be misidentified.",
                    "Lighting, camera angle, and fruit type affect accuracy.",
                    "Always double-check fruits manually."
                )

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    bulletPoints.forEach { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "\u2022",
                                fontSize = responsiveSp(12, 14, 16),
                                color = colors.textPrimary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                point,
                                fontFamily = poppinsFontFamily,
                                fontSize = responsiveSp(12, 14, 16),
                                color = colors.textPrimary,
                                textAlign = TextAlign.Start
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                }

                Spacer(modifier = Modifier.height(responsiveDp(20,24,24)))
            }
        }
}
@Composable
fun DisclaimerOverlay(
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    BackHandler(enabled = true) {
        isVisible = false
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(
                onClick = {
                    isVisible = false
                    onDismiss()
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animate only the About card
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(500, delayMillis = 100)),
            exit = fadeOut(animationSpec = tween(300)) +
                    scaleOut(animationSpec = tween(300))
        ) {
            val animatedScale by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0.8f,
                animationSpec = tween(400, easing = EaseOutBounce)
            )

            Disclaimer(
                modifier = Modifier
                    .scale(animatedScale)
                    .clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        }
    }
}
