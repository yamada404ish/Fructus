package com.example.fructus.ui.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fructus.ui.notification.model.Filter

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,      // This is the ViewModel that holds notification data and logic
    onNavigateUp: () -> Unit = {},         // Callback for the back button (default: do nothing)
    onArchiveClick: () -> Unit = {},
    onNotificationNavigate: (fruitId: Int) -> Unit = {}
) {

    val notifications by viewModel.notifications.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.clearNewFlag()
    }

    NotificationScreenContent(
        notifications = when (viewModel.filter) {       // ✅ apply filter at UI-level
            Filter.All -> notifications
            Filter.Unread -> notifications.filter { !it.isRead }
        },  // Show filtered notifications based on selected filter
        filter = viewModel.filter,                        // Current filter value (All or Unread)

        // When a new filter is selected, update it in the ViewModel
        onSelectedFilter = viewModel::onSelectFilter,

        // When a user taps a notification, mark it as read
        onNotificationClick = { notificationId, fruitId ->
            // ✅ Mark as read
            viewModel.markNotificationAsRead(notificationId)
            // ✅ Navigate to detail
            onNotificationNavigate(fruitId)
        },

        // When user taps "Mark All as Read", update all
        onMarkAllAsRead = viewModel::markAllAsRead,

        // Navigation callbacks for back and settings buttons
        onNavigateUp = onNavigateUp,

        // Callback for the archive button
        onArchiveClick = onArchiveClick
    )
}


/*
This file simply connects the ViewModel to the UI layer (NotificationScreenContent).

It passes all data and actions (like filter selection, reading notifications) from the NotificationViewModel to the UI.

It's a clean separation of UI logic from UI display.
*/
