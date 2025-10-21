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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun About(
    modifier: Modifier = Modifier

) {

    val colors = MaterialTheme.appColors
    // Define your team structure
    val teamSections = listOf(
        "Backend" to listOf("Adrian Miclat | Stephen Ortega", "Jenevieve Tonion"),
        "Frontend" to listOf("Jenevieve Tonion | Hannah Quinto"),
        "Resources" to listOf("Missy Tanhueco | Michael Sazon")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.15f)),
    ) {
        Card (
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
                .clickable (
                    onClick = { },
                    indication = null, // 🔥 disables ripple
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
                    "Development Team",
                    fontFamily = poppinsFontFamily,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Loop through each section
                teamSections.forEach { (role, members) ->
                    Text(
                        role,
                        fontFamily = poppinsFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    members.forEach { member ->
                        Text(
                            member,
                            fontFamily = poppinsFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

    }
}
@Composable
fun AboutOverlay(
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

            About(
                modifier = Modifier
                    .scale(animatedScale)
                    .clickable(
                        onClick = { /* block inside clicks */ },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        }
    }
}
