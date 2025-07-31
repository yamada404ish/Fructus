package com.example.fructus.ui.shared

import androidx.compose.foundation.background
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
import com.example.fructus.ui.screens.notification.model.Filter
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
            .clip(RoundedCornerShape(40))
            .background(
                if (isSelected) Color(0xFFDDCFAE) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

fun Filter.displayName(): String = when (this) {
    Filter.All -> "All"
    Filter.Unread -> "Unread"
}


@Preview
@Composable
private fun FilterChipPrev() {
    FructusTheme {

    }
}
