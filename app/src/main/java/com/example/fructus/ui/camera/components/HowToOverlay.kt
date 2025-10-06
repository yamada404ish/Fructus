package com.example.fructus.ui.camera.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToOverlay(
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    // Trigger fade-in when opened
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Background (dimmed)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        // Fade animation only (no slide)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 100)),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
        ) {
            val animatedScale by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0.8f,
                animationSpec = tween(400, easing = EaseOutBounce)
            )

            HowTo(
                onClose = {
                    isVisible = false
                    onDismiss()
                },
                modifier = Modifier
                    .scale(animatedScale)
                    .clickable(
                        onClick = { /* block internal card clicks */ },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        }
    }
}
