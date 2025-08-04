package com.example.fructus.ui.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fructus.R

@Composable
fun FruitStatus(
    ripeningProcess: Boolean,
    shelfLife: Int
) {
    Row {
        Image(
            painter = painterResource(
                if (ripeningProcess) R.drawable.natural_label else R.drawable.artificial_label
            ),
            contentDescription = null,
            modifier = Modifier.height(30.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        DaysLeft(shelfLife = shelfLife)
    }
}