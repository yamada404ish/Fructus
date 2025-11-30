package com.example.fructus.ui.camera.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.detail.components.HorizontalLine
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.responsiveDp
import com.example.fructus.util.responsiveSp

@Composable
fun Tips(modifier: Modifier = Modifier) {

    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        verticalArrangement = Arrangement.Center
    ) {
        val items = listOf(
            Triple(R.drawable.ic_temp, "Keep Cool", Pair("Store fruit in cool conditions", "Most fresh fruits last longer if stored in a cool place.") to "Source: unlockfood.ca"),
            Triple(R.drawable.ic_sun, "Avoid Sunlight", Pair("Keep fruits away from the sunlight", "Heat causes fruits to lose moisture...") to "Source: purdue.edu"),
            Triple(R.drawable.ic_faucet, "Wash and Dry", Pair("To prevent bacteria growth", "Wash fruits before storing or consuming.") to "Source: fda.gov")
        )

        items.forEachIndexed { index, item ->
            InfoCard(
                icon = item.first,
                title = item.second,
                subtitle = item.third.first.first,
                subtext = item.third.first.second,
                sourceText = item.third.second,
                isExpanded = expandedIndex == index,
                onToggle = {
                    expandedIndex = if (expandedIndex == index) null else index
                }
            )
        }
    }
}



@Composable
fun InfoCard(
    icon: Int,
    title: String,
    subtitle: String,
    subtext: String,
    sourceText: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.appColors
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.tip),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onToggle() }
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Tips icon",
                modifier = Modifier.size(responsiveDp(30,32,34))
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = responsiveSp(12,14,16),
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = subtitle,
                    fontSize = responsiveSp(8,10,12),
                    fontFamily = poppinsFontFamily,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
            }

            Icon(
                painter = painterResource(R.drawable.downdown),
                contentDescription = "arrow icon",
                modifier = Modifier
                    .rotate(rotationState)
                    .size(responsiveDp(14,16,18)),
                tint = colors.textTertiary
            )
        }

        // Expanded content
        if (isExpanded) {
            HorizontalLine(color = colors.bg)

            Text(
                text = subtext,
                fontFamily = poppinsFontFamily,
                fontSize = responsiveSp(10,12,14),
                color = colors.textPrimary,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = sourceText,
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = responsiveSp(8,10,12),
                color = colors.textPrimary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}



@Preview
@Composable
private fun TipsPrev() {
    FructusTheme {
        Tips(modifier = Modifier.fillMaxWidth())
    }
}
