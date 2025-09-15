package com.example.fructus.ui.home.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fructus.R
import com.example.fructus.ui.theme.appColors


@Composable
fun FructusLogo(modifier: Modifier = Modifier) {

    val colors = MaterialTheme.appColors

    Icon(
        painter = painterResource(R.drawable.fructus_logo),
        contentDescription = "Fructus Logo",
        modifier = modifier
            .height(20.dp) // Adjust height as needed
            .wrapContentWidth(),
        tint = colors.textPrimary
    )
}

@Preview
@Composable
private fun FructuslogoPrev() {
    FructusLogo()
}
