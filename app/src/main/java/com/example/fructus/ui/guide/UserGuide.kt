package com.example.fructus.ui.guide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun UserGuide(
    onNavigateUp: () -> Unit
) {

    var selectedGuide by remember { mutableStateOf("Banana") }
    var selectedProcess by remember { mutableStateOf("Natural")}

    UserGuideScreenContent(
        onNavigateUp = onNavigateUp,
        selectedGuide = selectedGuide,
        onGuideChange = {selectedGuide = it},

        selectedProcess = selectedProcess,
        onProcessChange = {selectedProcess = it}
    )
}
