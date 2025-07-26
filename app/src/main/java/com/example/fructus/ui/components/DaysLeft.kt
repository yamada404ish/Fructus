package com.example.fructus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.theme.FructusTheme

@Composable
fun DaysLeft(
    modifier: Modifier = Modifier,
    shelfLife: Int
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(30.dp)
            .background(MaterialTheme.colorScheme.onPrimary,RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "${shelfLife} day",
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}


@Preview
@Composable
private fun DaysLeftPrev() {
    FructusTheme {
//        DaysLeft()
    }
}