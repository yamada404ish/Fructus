package com.example.fructus.ui.setting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun ClearNotificationsDialog(
    onDismiss: () -> Unit,
    onClearAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Erase All Data?",
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 22.sp
                )
                Text(
                    text = "This action cannot be undone.",
                    fontFamily = poppinsFontFamily,
                    fontSize = 18.sp,
                    color = Color(0xFF7D7D7D)
                )
            }
        },
        confirmButton = {
            // Custom Row for left/right button alignment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cancel button (outlined)
                OutlinedButton(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF55D5D)
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color(0xFFF55D5D)
                    )
                }

                // Clear All button (filled red)
                Button(
                    onClick = onClearAll,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF55D5D),
                        contentColor = Color(0xFFFFFFFF)
                    )
                ) {
                    Text(
                        text = "Erase All",
                        fontSize = 18.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        },
        dismissButton = {},

        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
