package com.example.fructus.ui.notification

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.data.local.entity.NotificationEntity
import com.example.fructus.ui.notification.components.NotificationCard
import com.example.fructus.ui.notification.components.NotificationFilters
import com.example.fructus.ui.notification.model.Filter
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.calculateDaysSince

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreenContent(
    notifications: List<NotificationEntity>,
    onNotificationClick: (notificationId: Int, fruitId: Int) -> Unit,
    onMarkAllAsRead: () -> Unit,
    filter: Filter,
    onArchiveCount: Int = 0,
    onSelectedFilter: (Filter) -> Unit,
    onNavigateUp: () -> Unit = {},
    onArchiveClick: () -> Unit = {}
) {
    val recent = notifications.filter { calculateDaysSince(it.timestamp) <= 1 }
    val earlier = notifications.filter { calculateDaysSince(it.timestamp) > 1 }

    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(
                                onClick = onNavigateUp,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    )
                },
                title = {
                    Text(
                        text = "Notifications",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        letterSpacing = 0.1.sp,
                        color = colors.textPrimary
                    )
                },
                actions = {
                    Box {
                        Icon(
                            painter = painterResource(R.drawable.ic_archive),
                            contentDescription = "Archive",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(
                                    onClick = onArchiveClick,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ),
                            tint = colors.textTertiary
                        )

                        if (onArchiveCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 0.dp, y = (-6).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE74C3C), shape = RoundedCornerShape(50))
                                        .padding(horizontal = 7.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (onArchiveCount > 99) "99+" else onArchiveCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = poppinsFontFamily,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            // Filters row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NotificationFilters(
                    items = Filter.entries.toList(),
                    selectedFilter = filter,
                    onSelectedFilter = onSelectedFilter
                )

                if (notifications.isNotEmpty()) {
                    Text(
                        "✓ Mark all as read",
                        fontSize = 14.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.textTertiary,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onMarkAllAsRead()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (notifications.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon (
                        painter = painterResource(R.drawable.empty),
                        contentDescription = "No notification available",
                        modifier = Modifier.size(200.dp),
                        tint = colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No notification available",
                        color = colors.textSecondary,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Notifications list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),

                ) {
                    if (recent.isNotEmpty()) {
                        item {
                            Text(
                                "Recent",
                                fontSize = 20.sp,
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = colors.textPrimary
                            )
                        }
                        items(recent) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = { onNotificationClick(notification.id, notification.fruitId) }
                            )
                        }
                    }

                    if (earlier.isNotEmpty()) {
                        item {
                            Text(
                                "Earlier",
                                fontSize = 20.sp,
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = colors.textPrimary
                            )
                        }
                        items(earlier) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = { onNotificationClick(notification.id, notification
                                    .fruitId) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(14.dp)) }
                }
            }
        }
    }
}


