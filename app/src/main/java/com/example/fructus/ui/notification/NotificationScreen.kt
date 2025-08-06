package com.example.fructus.ui.notification

import androidx.compose.runtime.Composable

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,      // This is the ViewModel that holds notification data and logic
    onNavigateUp: () -> Unit = {},         // Callback for the back button (default: do nothing)
    onSettingsClick: () -> Unit = {}       // Callback for the settings button (default: do nothing)
) {

    NotificationScreenContent(
        notifications = viewModel.filteredNotifications,  // Show filtered notifications based on selected filter
        filter = viewModel.filter,                        // Current filter value (All or Unread)

        // When a new filter is selected, update it in the ViewModel
        onSelectedFilter = viewModel::onSelectFilter,

        // When a user taps a notification, mark it as read
        onNotificationClick = viewModel::markNotificationAsRead,

        // When user taps "Mark All as Read", update all
        onMarkAllAsRead = viewModel::markAllAsRead,

        // Navigation callbacks for back and settings buttons
        onNavigateUp = onNavigateUp,
        onSettingsClick = onSettingsClick
    )
}


/*
This file simply connects the ViewModel to the UI layer (NotificationScreenContent).

It passes all data and actions (like filter selection, reading notifications) from the NotificationViewModel to the UI.

It's a clean separation of UI logic from UI display.
*/
