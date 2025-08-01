package com.example.fructus.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fructus.R


@Composable
fun FructusLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.fructus_logo),
        contentDescription = "Fructus Logo",
        modifier = modifier
            .height(36.dp) // Adjust height as needed
            .wrapContentWidth()
    )
}

@Preview
@Composable
private fun FructuslogoPrev() {
    FructusLogo()
}
