package com.example.fructus.ui.guide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun UserGuide(
    viewModel: UserGuideViewModel,
    onNavigateUp: () -> Unit
) {

    var selectedGuide by remember { mutableStateOf("Banana") }

    UserGuideScreenContent(
        viewModel = viewModel,
        onNavigateUp = onNavigateUp,
        selectedGuide = selectedGuide,
        onGuideChange = {selectedGuide = it}
    )
}
