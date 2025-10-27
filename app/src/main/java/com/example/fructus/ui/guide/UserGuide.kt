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

    if (selectedGuide == "Banana" || selectedGuide == "Tomato") {
        if (selectedProcess != "Natural") {
            selectedProcess = "Natural"
        }
    }

    UserGuideScreenContent(
        onNavigateUp = onNavigateUp,
        selectedGuide = selectedGuide,
        onGuideChange = { newGuide ->
            selectedGuide = newGuide
            if (newGuide == "Banana" || newGuide == "Tomato") {
                selectedProcess = "Natural"
            }
        },
        selectedProcess = selectedProcess,
        onProcessChange = {selectedProcess = it}
    )
}
