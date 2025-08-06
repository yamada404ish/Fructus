package com.example.fructus.ui

import androidx.compose.runtime.Composable
import com.example.fructus.navigation.FructusNav
import com.example.fructus.ui.theme.FructusTheme

@Composable
fun FructusApp() {
    FructusTheme {
        FructusNav()
    }
}
