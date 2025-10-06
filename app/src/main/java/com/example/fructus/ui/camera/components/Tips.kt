package com.example.fructus.ui.camera.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.shared.InfoCard
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun Tips(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        InfoCard(icon = R.drawable.ic_temp, title = "Keep Refrigerated", subtitle = "Store fruit in cool conditions")
        InfoCard(icon = R.drawable.ic_sun, title = "Avoid Sunlight", subtitle = "Keep fruits away from the sunlight")
        InfoCard(icon = R.drawable.ic_faucet,title = "Wash and Dry", subtitle = "To prevent bacteria growth")
    }
}


@Composable
fun InfoCard(
    icon: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.appColors

    Card(
        modifier = modifier
            .height(110.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.tip),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Tips icon",
                modifier = Modifier
                    .size(38.dp)

            )

            Text(
                text = title,
                fontSize = 8.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 8.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )
        }
    }
}


@Preview
@Composable
private fun TipsPrev() {
    FructusTheme {
        Tips(modifier = Modifier.fillMaxWidth())
    }
}
