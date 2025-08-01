package com.example.fructus.ui.notification.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun NotificationCardYesterday(
    image: Painter,
    itemName: String,
    daysLeft: Int,
) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),


    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image (
                painter = image,
                contentDescription = "Item Image",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop

            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = "$itemName is spoiling in $daysLeft days",
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun NotificationListYesterday() {
    val image = painterResource(id = R.drawable.fruit) // Replace with your own image

    NotificationCardYesterday(
        image = image,
        itemName = "Cavendish",
        daysLeft = 2,
    )
}


@Preview
@Composable
private fun NotificationCardYesterdayPrev() {
    FructusTheme {
        NotificationListYesterday()
    }
}


