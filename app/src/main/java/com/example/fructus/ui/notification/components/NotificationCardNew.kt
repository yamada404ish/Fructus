package com.example.fructus.ui.notification.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.data.Fruit
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun NotificationCard(
    fruit: Fruit,
    isRead: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (isRead) Color.White else Color(0xFFFFF8E1)

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick()}
            .height(80.dp),
        elevation = CardDefaults.cardElevation(1.dp),

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),


    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .background(
                    color = Color.Transparent,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image (
                painter = painterResource(fruit.image),
                contentDescription = "Item Image",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop

            )
            Spacer(modifier = Modifier.size(12.dp))
            Column (
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = "Your ${fruit.name} is starting to spoil—best to use it soon!",
                    fontSize = 12.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.1.sp,
                    lineHeight = 13.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding (top = 6.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "1h ago",
                    fontSize = 10.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 13.sp,
                    color = Color(0xFF4E4E4E)
                )
            }

        }
    }
}


@Composable
fun NotificationListNew() {

    NotificationCard(
        fruit = Fruit(
            id = 0,
            name = "Lakatan",
            shelfLife = 2,
            ripeningProcess = true,
            image = R.drawable.img_placeholder,
            ripeningStage = "unripe",
        ),
        isRead = false,
        onClick = {}
    )
}


@Preview
@Composable
private fun NotificationCardPrev() {
    FructusTheme {
        NotificationListNew()
    }
}


