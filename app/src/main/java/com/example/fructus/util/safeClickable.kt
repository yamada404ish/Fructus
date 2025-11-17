package com.example.fructus.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope

@Composable
fun Modifier.safeClickable(
    clickGuard: ClickGuard,
    coroutineScope: CoroutineScope,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    return if (enabled) {
        this.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            clickGuard.tryLock(coroutineScope) { onClick() }
        }
    } else this
}
