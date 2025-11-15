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

@Composable
fun Tips(modifier: Modifier = Modifier) {
    Column (
        verticalArrangement = Arrangement.Center
    ) {
        InfoCard(icon = R.drawable.ic_temp, title = "Keep Cool", subtitle = "Store fruit in cool " +
                "conditions", subtext = "Most fresh fruits last longer if stored in a cool place", sourceText = "Source: unlockfood.ca")
        InfoCard(icon = R.drawable.ic_sun, title = "Avoid Sunlight", subtitle = "Keep fruits away" +
                " from the sunlight", subtext = "Heat causes fruits to lose their moisture, so it is best to keep your fruits away from sunlight.", sourceText = "Source: purdue.edu")
        InfoCard(icon = R.drawable.ic_faucet,title = "Wash and Dry", subtitle = "To prevent " +
                "bacteria growth", subtext = "Fruits may arrive contaminated with bacteria, it is" +
                " best to wash them before storing or consuming.", sourceText = "Source: fda.gov")
    }
}


@Composable
fun InfoCard(
    icon: Int,
    title: String,
    subtitle: String,
    subtext: String,
    sourceText:String,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.appColors
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState (
        targetValue = if (expandedState) 180f else 0f
    )

    Card(
        modifier = modifier
//            .height(110.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween (
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.tip),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Tips icon",
                modifier = Modifier
                    .size(32.dp)

            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
            Icon(
                painter = painterResource(R.drawable.downdown),
                contentDescription = "down icon",
                modifier = Modifier
                    .rotate(rotationState)
                    .size(16.dp),
                tint = colors.textTertiary
            )
        }
        if (expandedState) {
            HorizontalLine(
                color = colors.bg
            )
            Text (
                text = subtext,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colors.textPrimary,
                modifier =  (
                Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
                )
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = sourceText,
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 10.sp,
                color = colors.textPrimary,
                modifier =  (
                Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                )
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
