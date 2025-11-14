package com.example.fructus.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.AppColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun ScreenTopBar(
    title: String,
    onNavigateUp: () -> Unit,
    colors: AppColors,
    modifier: Modifier = Modifier,
    showArchive: Boolean = false,
    archiveCount: Int = 0,
    onArchiveClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 80.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = colors.textPrimary,
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    onClick = onNavigateUp,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        // Title
        Text(
            text = title,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            letterSpacing = 0.1.sp,
            color = colors.textPrimary
        )

        // Optional archive icon
        if (showArchive && onArchiveClick != null) {
            Box {
                Icon(
                    painter = painterResource(R.drawable.ic_archive),
                    contentDescription = "Archive",
                    tint = colors.textTertiary,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            onClick = onArchiveClick,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )

                if (archiveCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-10).dp)
                            .background(Color(0xFFE74C3C), shape = RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (archiveCount > 9) "9+" else archiveCount.toString(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = poppinsFontFamily
                        )
                    }
                }
            }
        } else {
            // Empty box to keep spacing consistent
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}
