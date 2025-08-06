package com.example.fructus.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fructus.data.DummyFruitDataSource
import com.example.fructus.data.FruitNotification
import com.example.fructus.ui.notification.model.Filter

class NotificationViewModel : ViewModel() {

    // This is observed by Compose UI
    var filter by mutableStateOf(Filter.All)
        private set


    // Holds a list of fruit notifications
    var notifications = mutableStateListOf<FruitNotification>()
        private set

    init {
        // We only want to show fruits with shelfLife <= 24 (less than or equal to 1 day)
        DummyFruitDataSource.fruitList
            .filter { it.shelfLife <= 24 } // Filter only fruits that are about to expire
            .forEach {
                // For each filtered fruit, create a notification and mark it as unread
                notifications.add(
                    FruitNotification(
                        fruit = it,
                        isRead = false // Mark all new notifications as unread
                    )
                )
            }
    }


    fun onSelectFilter(newFilter: Filter) {
        filter = newFilter // Update the current filter (All or Unread)
    }


    fun markNotificationAsRead(fruitId: Int) {
        // Find the index of the notification with that fruitId
        val index = notifications.indexOfFirst { it.fruit.id == fruitId }

        // If we find it (index is not -1), mark it as read
        if (index != -1) {
            // Replace that notification with a new one marked as read
            notifications[index] = notifications[index].copy(isRead = true)
        }
    }


    fun markAllAsRead() {
        // Replace every item in the list with a copy marked as read
        notifications.replaceAll { it.copy(isRead = true) }
    }


    val filteredNotifications: List<FruitNotification>
    get() = when (filter) {
        Filter.All -> notifications // Return all notifications
        Filter.Unread -> notifications.filter { !it.isRead } // Only return unread ones
    }
}

/*
This NotificationViewModel holds a list of fruit notifications.

It filters fruits based on shelf life.

It lets you filter the list, mark single or all notifications as read.

Compose automatically reacts when these values change.*/
