package com.example.fructus.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AppBackgroundScaffold(
    backgroundColor: Color = Color(0xFFF0EFE9), // your solid background color
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // set the solid color background
    ) {
        content()
    }
}
