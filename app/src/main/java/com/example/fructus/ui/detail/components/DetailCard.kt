package com.example.fructus.ui.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DetailCard(fruitName: String) {

    val context = LocalContext.current

    val imageResId = remember (fruitName) {
        context.resources.getIdentifier(
            fruitName.lowercase(),
            "drawable",
            context.packageName
        )
    }
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(if (fruitName == "tomato" || fruitName == "Tomato") Color(0xFFFFC8A4) else
                Color(0xFFFFE586),
                RoundedCornerShape(24.dp))
            .height(200.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .offset(y = 40.dp)
        )
    }
}
