package com.example.fructus.ui.shared


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
enum class RipenessStage(val displayName: String) {
    UNRIPE("Unripe"),
    RIPE("Ripe"),
    OVERRIPE("Overripe"),
    SPOILED("Spoiled")
}


@Composable
fun RipenessProgressBar(
    currentStage: RipenessStage,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    cornerRadius: Dp = 10.dp,
    segmentSpacing: Dp = 4.dp,
    totalWidth: Dp = 260.dp // 👈 controls how wide the whole bar+labels are
) {
    // Update colors based on current stage - only current stage lights up
    val segmentColors = RipenessStage.entries.map { stage ->
        when {
            stage == currentStage -> when (stage) {
                RipenessStage.UNRIPE -> Color(0xFF4CAF50)      // Green
                RipenessStage.RIPE -> Color(0xFFFFC107)        // Yellow
                RipenessStage.OVERRIPE -> Color(0xFFFF9800)    // Orange
                RipenessStage.SPOILED -> Color(0xFFF44336)     // Red
            }
            else -> Color(0xFFD1CEBA)
        }
    }

    Column(
        modifier = modifier.width(totalWidth), // 👈 fixed width keeps it centered
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Segmented progress bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            horizontalArrangement = Arrangement.spacedBy(segmentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RipenessStage.entries.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(segmentColors[index])
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stage labels (aligned under each segment)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(segmentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RipenessStage.entries.forEach { stage ->
                val isActive = stage == currentStage
                val textColor = if (isActive) Color.Black else Color(0xFF9AA08C)

                // 👇 Each label gets equal weight (same as segments above)
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stage.displayName,
                        fontSize = 12.sp,
                        fontFamily = poppinsFontFamily,
                        color = textColor,
                        fontWeight = FontWeight.Normal
                    )
                }
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