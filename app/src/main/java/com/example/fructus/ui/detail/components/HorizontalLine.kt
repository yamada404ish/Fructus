package com.example.fructus.ui.detail.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalLine(
    color: Color
) {
    HorizontalDivider(
        thickness = 2.dp,
        color = color
    )
}