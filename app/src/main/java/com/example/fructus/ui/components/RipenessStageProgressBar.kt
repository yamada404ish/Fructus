package com.example.fructus.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    height: Dp = 10.dp,
    backgroundColor: Color = Color(0xFFE0E0E0),
    cornerRadius: Dp = 10.dp,
    segmentSpacing: Dp = 4.dp
) {
    val stageColors = mapOf(
        RipenessStage.UNRIPE to Color(0xFFE0E0E0),      // Light gray for inactive
        RipenessStage.RIPE to Color(0xFFFFC107),        // Yellow/Amber for active
        RipenessStage.OVERRIPE to Color(0xFFE0E0E0),    // Light gray for inactive
        RipenessStage.SPOILED to Color(0xFFE0E0E0)      // Light gray for inactive
    )

    // Update colors based on current stage - only current stage lights up
    val segmentColors = RipenessStage.values().map { stage ->
        when {
            stage == currentStage -> when (stage) {
                RipenessStage.UNRIPE -> Color(0xFF4CAF50)      // Green
                RipenessStage.RIPE -> Color(0xFFFFC107)        // Yellow
                RipenessStage.OVERRIPE -> Color(0xFFFF9800)    // Orange
                RipenessStage.SPOILED -> Color(0xFFF44336)     // Red
            }
            else -> backgroundColor
        }
    }

    Column(modifier = modifier) {
        // Segmented progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(segmentSpacing)
        ) {
            RipenessStage.values().forEachIndexed { index, stage ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(height)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(segmentColors[index])
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stage labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RipenessStage.values().forEach { stage ->
                val isActive = stage == currentStage
                val textColor = if (isActive) {
                    Color.Black
                } else {
                    Color.Gray
                }

                Text(
                    text = stage.displayName,
                    fontSize = 14.sp,
                    color = textColor,
                    fontWeight = FontWeight.Normal
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
            currentStage = RipenessStage.RIPE,
            modifier = Modifier.fillMaxWidth()
        )
    }
}