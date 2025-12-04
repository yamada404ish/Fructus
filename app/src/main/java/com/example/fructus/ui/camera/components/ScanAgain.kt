package com.example.fructus.ui.camera.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.fructus.util.responsiveSp

@Composable
fun ScanAgain(
    isDarkMode: Boolean,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    val colors = MaterialTheme.appColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.card
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Title
                Text(
                    text = "Would you like to scan another fruit?",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFontFamily,
                    fontSize = responsiveSp(18, 22, 24),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(24.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // CANCEL
                    OutlinedButton(
                        onClick = onNo,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, colors.button),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFBADBA2)
                        )
                    ) {
                        Text(
                            "No",
                            fontFamily = poppinsFontFamily,
                            fontSize = responsiveSp(14, 18, 20),
                            color = if (isDarkMode) Color.White else Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // ERASE ALL
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
                            "Yes",
                            fontFamily = poppinsFontFamily,
                            fontSize = responsiveSp(14, 18, 20),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
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
