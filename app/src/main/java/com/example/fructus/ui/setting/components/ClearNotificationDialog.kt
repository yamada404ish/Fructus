package com.example.fructus.ui.setting.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun ClearNotificationsDialog(
    onDismiss: () -> Unit,
    onClearAll: () -> Unit
) {
    val colors = MaterialTheme.appColors

    // ✅ Custom Dialog: Not dismissable by tapping outside
    Dialog(
        onDismissRequest = {
            // Prevent closing on outside tap
        }
    ) {
        // ✅ Handle Back button manually
        BackHandler(enabled = true) {
            onDismiss()
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colors.card
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Erase All Data?",
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 22.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "This action cannot be undone.",
                    fontFamily = poppinsFontFamily,
                    fontSize = 18.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
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

                    Spacer(modifier = Modifier.width(22.dp))


                    Button(
                        onClick = onClearAll,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF55D5D),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Erase All",
                            fontSize = 18.sp,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
