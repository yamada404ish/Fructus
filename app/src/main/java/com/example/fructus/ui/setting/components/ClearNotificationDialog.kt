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
import androidx.compose.material3.MaterialTheme
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
                    text = "Clear All Notifications?",
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsFontFamily,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "This action cannot be undone.",
                    fontFamily = poppinsFontFamily,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        },
        confirmButton = {
            // Custom Row for left/right button alignment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cancel button (outlined)
                OutlinedButton(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF4055)
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF4055)
                    )
                }

                // Clear All button (filled red)
                Button(
                    onClick = onClearAll,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4055),
                        contentColor = Color(0xFFFFFFFF)
                    )
                ) {
                    Text(
                        text = "Clear All",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        },
        dismissButton = {},

        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
