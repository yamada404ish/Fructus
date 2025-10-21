package com.example.fructus.ui.camera.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun ScanAgain(
    isDarkMode: Boolean,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    val colors = MaterialTheme.appColors

    AlertDialog(
        onDismissRequest = {  },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Would you like to scan another fruit?",
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 22.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNo,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, colors.button),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFBADBA2)
                    ),
                ) {
                    Text(
                        text = "No",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                }

                Button(
                    onClick = onYes,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.button,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Yes",
                        fontSize = 18.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        },
        dismissButton = {},
        containerColor = colors.card,
        shape = RoundedCornerShape(16.dp)
    )
}


@Preview
@Composable
private fun ScanAgainPrev() {
    FructusTheme {
        ScanAgain(
            isDarkMode = false,
            onYes = {},
            onNo = {}
        )
    }
}
