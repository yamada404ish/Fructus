package com.example.fructus.ui.screens.notification

import androidx.compose.runtime.mutableStateListOf
import com.example.fructus.data.DummyFruitDataSource
import com.example.fructus.data.FruitNotification

val notificationList = mutableStateListOf<FruitNotification>()

fun loadNotificationsIfNeeded() {
    if (notificationList.isEmpty()) {
        DummyFruitDataSource.fruitList.forEach { fruit ->
            if (fruit.shelfLife <= 24) {
                notificationList.add(FruitNotification(fruit, isRead = false))
            }
        }
    }
}