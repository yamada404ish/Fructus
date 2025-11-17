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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.ClickGuard
import com.example.fructus.util.safeClickable

@Composable
fun BottomNavBar(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    hasNewNotification: Boolean,
    isDarkMode: Boolean,
    clickGuard: ClickGuard,

) {
    val colors = MaterialTheme.appColors
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Custom curved background with conditional shadow
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

            // Draw shadow for both modes with appropriate colors
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    isAntiAlias = true
                    setShadowLayer(
                        10f,
                        0f,
                        -1f,
                        if (isDarkMode) android.graphics.Color.DKGRAY else android.graphics.Color.LTGRAY
                    )
                }

                // Required for shadow to show
                canvas.nativeCanvas.setDrawFilter(android.graphics.PaintFlagsDrawFilter(0, android.graphics.Paint.ANTI_ALIAS_FLAG))

                canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
            }

            // Draw the actual path with theme-appropriate color
            drawPath(path, color = colors.surface)
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
                    .safeClickable(clickGuard, coroutineScope) {
                        onNotificationClick()
                    }
            ) {
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notification_bell),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(34.dp),
                        tint = colors.textTertiary
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
                    color = colors.textTertiary, // Use theme color instead of hardcoded
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Settings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .safeClickable(clickGuard, coroutineScope) {
                        onSettingsClick()
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(34.dp),
                    tint = colors.textTertiary
                )
                Text(
                    text = "  Settings   ",
                    fontSize = 11.sp,
                    color = colors.textTertiary, // Use theme color instead of hardcoded
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

//@Preview
//@Composable
//private fun NavbarPrev() {
//    FructusTheme {
//        BottomNavBar(
//            onNotificationClick = {},
//            onSettingsClick = {},
//            hasNewNotification = false,
//            isDarkMode = false
//        )
//
//    }
//
//}