@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fructus.ui.screens.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.theme.FructusTheme

@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    Scaffold (
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 50.dp),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                },
                title = {}
            )
        }
    ){}
}

@Preview
@Composable
private fun NotificationScreenPrev() {
    FructusTheme {
        NotificationScreen()

    }

}