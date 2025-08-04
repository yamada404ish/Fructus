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

    var filter by mutableStateOf(Filter.All)
        private set

    var notifications = mutableStateListOf<FruitNotification>()
        private set

    init {
        // Load notifications based on shelfLife <= 24
        DummyFruitDataSource.fruitList
            .filter { it.shelfLife <= 24 }
            .forEach {
                notifications.add(FruitNotification(it, isRead = false))
            }
    }

    fun onSelectFilter(newFilter: Filter) {
        filter = newFilter
    }

    fun markNotificationAsRead(fruitId: Int) {
        val index = notifications.indexOfFirst { it.fruit.id == fruitId }
        if (index != -1) {
            notifications[index] = notifications[index].copy(isRead = true)
        }
    }

    fun markAllAsRead() {
        notifications.replaceAll { it.copy(isRead = true) }
    }

    val filteredNotifications: List<FruitNotification>
        get() = when (filter) {
            Filter.All -> notifications
            Filter.Unread -> notifications.filter { !it.isRead }
        }
}
