package com.example.fructus.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun FructusGuideStep(
    iconRes: Painter,
    title: String,
    description: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = iconRes,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFontFamily,
                color = Color.Black
            )
            Text(
                text = description,
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily,
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )
        }
    }
}