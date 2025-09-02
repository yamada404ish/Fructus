package com.example.fructus.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun BottomNavBar(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    hasNewNotification: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        // Custom curved background with rounded corners
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val corner = 10.dp.toPx()
            val notchWidth = 190.dp.toPx()
            val notchDepth = 58.dp.toPx()
            val notchFlat = 40.dp.toPx()
            val cx = w / 2f

            val controlOffset = 32.dp.toPx() // controls curve smoothness

            val path = Path().apply {
                moveTo(corner, 0f)

                // Line to start of notch
                lineTo(cx - notchWidth / 2f, 0f)

                // Left curve into notch (smoother)
                cubicTo(
                    cx - notchWidth / 2f + controlOffset, 0f,
                    cx - notchFlat / 2f - controlOffset, notchDepth,
                    cx - notchFlat / 2f, notchDepth
                )

                // Flat bottom
                lineTo(cx + notchFlat / 2f, notchDepth)

                // Right curve out of notch (smoother)
                cubicTo(
                    cx + notchFlat / 2f + controlOffset, notchDepth,
                    cx + notchWidth / 2f - controlOffset, 0f,
                    cx + notchWidth / 2f, 0f
                )

                // Line to top-right corner
                lineTo(w - corner, 0f)

                // Top-right corner
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(w - 2 * corner, 0f, w, 2 * corner),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Right edge
                lineTo(w, h)

                // Bottom edge
                lineTo(0f, h)

                // Left edge
                lineTo(0f, corner)

                // Top-left corner
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(0f, 0f, 2 * corner, 2 * corner),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                close()
            }

            drawPath(path, color = Color.White)
//            drawPath(path, color = Color.Blue, style = Stroke(width = 2.dp.toPx()))
        }


        // Navigation items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notifications
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable { }
//                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bell),
                        contentDescription = "Notifications",
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { onNotificationClick() },
                        tint = Color.Unspecified
                    )

                    if (hasNewNotification) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                        )
                    }
                }
                Text(
                    text = "Notifications",
                    fontSize = 11.sp,
                    color = Color(0xFF697F59),
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Settings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {  }
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(34.dp)
                        .clickable(
                            onClick = { onSettingsClick() },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
                Text(
                    text = "  Settings   ",
                    fontSize = 11.sp,
                    color = Color(0xFF697F59),
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

    }
}



/*

put an dot indicator if there is a new notif for the user

*/