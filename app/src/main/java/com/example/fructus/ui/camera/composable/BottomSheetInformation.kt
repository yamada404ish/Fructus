package com.example.fructus.ui.camera.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetInformationWithButtons(
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLife: Int,
    onResumeScanning: () -> Unit,
    onSave: () -> Unit
) {
    Column {
        // Buttons at the top of the bottom sheet
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onResumeScanning) {
                Text("Resume Scanning")
            }
            Button(onClick = onSave) {
                Text("Save")
            }
        }

        // Original bottom sheet content
//        BottomSheetInformation(
//            fruitName = fruitName,
//            ripeningStage = ripeningStage,
//            ripeningProcess = ripeningProcess,
//            shelfLife = shelfLife
//        )
    }
}