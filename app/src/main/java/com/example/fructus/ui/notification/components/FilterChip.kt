package com.example.fructus.ui.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12))
            .background(
                if (isSelected) Color(0xFFBADBA2) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
            )
            .border(     // ✅ Outline for unselected
                width = if (isSelected) 0.dp else 1.dp,
                color = Color(0xFF718860),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
private fun FilterChipPrev() {
    FructusTheme {

    }
}
