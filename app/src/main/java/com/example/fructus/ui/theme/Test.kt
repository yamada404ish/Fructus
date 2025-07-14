package com.example.fructus.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fructus.ui.components.FructusLogo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .padding(WindowInsets.statusBars.asPaddingValues())
            ) {
                CenterAlignedTopAppBar(
                    title = { FructusLogo() }
                )
            }
        }
    ) {}
}



@Preview
@Composable
private fun TestScreenPrev() {
    FructusTheme {
        TestScreen()
    }
}