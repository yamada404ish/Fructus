package com.example.fructus.ui.notification.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.data.local.entity.NotificationEntity
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.formatTimeAgo
import com.example.fructus.util.getFruitDrawableId

@Composable
fun NotificationCard(
    notification: NotificationEntity,
    onClick: () -> Unit = {},

) {
    val backgroundColor = if (notification.isRead) Color.Transparent else Color(0xFFD0EFB9)

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (notification.isRead) 1.dp else 0.dp,
                color = if (notification.isRead) Color(0xFF718860) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )

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
                painter = painterResource(getFruitDrawableId(notification.fruitName)),
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
                    text = notification.message,
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
                    formatTimeAgo(notification.timestamp),
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



