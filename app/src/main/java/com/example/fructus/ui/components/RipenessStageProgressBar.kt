package com.example.fructus.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.FructusTheme

@OptIn(ExperimentalMaterial3Api::class)
// Enum to represent ripeness stages
enum class RipenessStage(val displayName: String, val progress: Float) {
    UNRIPE("Unripe", 0.25f),
    RIPE("Ripe", 0.5f),
    OVERRIPE("Overripe", 0.75f),
    SPOILED("Spoiled", 1.0f)
}

@Composable
fun RipenessProgressBar(
    currentStage: RipenessStage,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    backgroundColor: Color = Color(0xFFE0E0E0),
    cornerRadius: Dp = 6.dp
) {
    val progressColor = when (currentStage) {
        RipenessStage.UNRIPE -> Color(0xFF4CAF50)      // Green
        RipenessStage.RIPE -> Color(0xFFFFC107)        // Amber/Yellow
        RipenessStage.OVERRIPE -> Color(0xFFFF9800)    // Orange
        RipenessStage.SPOILED -> Color(0xFFF44336)     // Red
    }

    Column(modifier = modifier) {
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(currentStage.progress)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stage labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RipenessStage.values().forEach { stage ->
                Text(
                    text = stage.displayName,
                    fontSize = 12.sp,
                    color = if (stage == currentStage) progressColor else Color.Gray,
                    fontWeight = if (stage == currentStage) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProgressPrev() {
    FructusTheme {
        RipenessProgressBar(
            currentStage = RipenessStage.UNRIPE,
            modifier = Modifier.fillMaxWidth()
        )
    }
}