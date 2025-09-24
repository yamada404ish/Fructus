package com.example.fructus.ui.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.appColors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12))
            .background(
                if (isSelected) Color(0xFFBADBA2) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = colors.stroke,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable (
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = if (isSelected) {
                Color(0xFF2D4A1F)
            } else {

                colors.textPrimary
            }
        )
    }
}

