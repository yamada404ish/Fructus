package com.example.fructus.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun FruitFilterToggle(
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        listOf("All", "Spoiled").forEach { label ->
            val isSelected = selected == label
            Box(
                modifier = Modifier
                    .clickable (
                        onClick = {onSelect(label)},
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .background(
                        color = if (isSelected) Color(0xFFBADBA2) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(     // ✅ Outline for unselected
                        width = if (isSelected) 0.dp else 1.dp,
                        color = Color(0xFF718860),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}