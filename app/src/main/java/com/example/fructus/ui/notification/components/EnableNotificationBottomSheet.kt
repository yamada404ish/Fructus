package com.example.fructus.ui.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnableNotificationBottomSheet(
    onEnableClick: () -> Unit,
    onDismissClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .padding(top = 20.dp, bottom = 40.dp),
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Directly check notifications from your home screen",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = poppinsFontFamily,
                color = Color.Black,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enable push notifications to receive reminders before your fruit spoils. This app will not collect nor share any data.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = poppinsFontFamily,
                color = Color(0xFF7D7D7D),
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Enable Button
            Box (
                modifier = Modifier
                    .fillMaxWidth()
//                .padding(horizontal = 20.dp)
                    .height(58.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF0B52D))
                    .clickable { onEnableClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enable notifications",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontFamily = poppinsFontFamily
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Keep Disabled Button
            Text(
                text = "Keep Disabled",
                modifier = Modifier.clickable {
                    onDismissClick()
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = poppinsFontFamily,
                color = Color(0xFF746D6D)
            )
        }
    }
}

@Preview
@Composable
private fun EnableNotificationBottomSheetPrev() {
    FructusTheme {
//        EnableNotificationBottomSheet(
//            onEnableClick = {},
//            onDismissClick = {},
//            onDismissRequest = {}
//        )
    }
}
