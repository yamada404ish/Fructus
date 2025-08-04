package com.example.fructus.ui.notification

import androidx.compose.runtime.Composable

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNavigateUp: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    NotificationScreenContent(
        notifications = viewModel.filteredNotifications,
        filter = viewModel.filter,
        onSelectedFilter = viewModel::onSelectFilter,
        onNotificationClick = viewModel::markNotificationAsRead,
        onMarkAllAsRead = viewModel::markAllAsRead,
        onNavigateUp = onNavigateUp,
        onSettingsClick = onSettingsClick
    )
}
