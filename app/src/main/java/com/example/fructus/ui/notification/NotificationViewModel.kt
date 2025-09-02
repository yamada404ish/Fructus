package com.example.fructus.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.data.local.entity.NotificationEntity
import com.example.fructus.ui.notification.model.Filter
import com.example.fructus.util.getShelfLifeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val fruitDao: FruitDao,
    private val notificationDao: NotificationDao
) : ViewModel() {

    var filter by mutableStateOf(Filter.All)
        private set

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications



    init {
        // 1. Watch fruits and trigger notifications
        viewModelScope.launch {
            fruitDao.getAllFruits().collect { fruits ->
                fruits.forEach { fruit ->
                    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
                    val estimatedShelfLife = shelfLifeRange.minDays

                    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
                    val remainingShelfLife = estimatedShelfLife - daysSinceScan

                    val message: String? = when {
                        remainingShelfLife > 1 -> null
                        remainingShelfLife == 1 -> "${fruit.name} has only 1 day left!"
                        remainingShelfLife == 0 -> "${fruit.name} is spoiled!"
                        else -> null
                    }

                    if (message != null) {
                        val latestNotification = notificationDao.getLatestNotificationForFruit(fruit.name)
                        if (latestNotification == null || latestNotification.message != message) {
                            val notification = NotificationEntity(
                                fruitName = fruit.name,
                                message = message,
                                isRead = false,
                                scannedDate = fruit.scannedDate,
                                scannedTime = fruit.scannedTime,
                                timestamp = System.currentTimeMillis()
                            )
                            notificationDao.insertNotification(notification)
                        }
                    }
                }
            }
        }

        // 2. Watch notifications and enhance messages dynamically
        viewModelScope.launch {
            notificationDao.getAllNotifications().collect { list ->
                _notifications.value = list.map { enhanceMessage(it) }
            }
        }
    }

    // --- Enhancer for spoiled notifications ---
    private fun enhanceMessage(notification: NotificationEntity): NotificationEntity {
        val daysAgo = calculateDaysSince(notification.timestamp)
        return if (notification.message.contains("spoiled", ignoreCase = true) && daysAgo > 1) {
            val newMessage = when {
                daysAgo in 2..6 -> "${notification.fruitName} has been spoiled for days"
                daysAgo in 7..13 -> "${notification.fruitName} has been spoiled for a week"
                daysAgo >= 14 -> "${notification.fruitName} has been spoiled for weeks"
                else -> notification.message
            }
            notification.copy(message = newMessage)
        } else {
            notification
        }
    }

    fun onSelectFilter(newFilter: Filter) {
        filter = newFilter
    }

    fun markNotificationAsRead(notificationId: Int) {
        viewModelScope.launch {
            val notification = _notifications.value.find { it.id == notificationId }
            notification?.let {
                notificationDao.updateNotification(it.copy(isRead = true))
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _notifications.value.forEach { n ->
                notificationDao.updateNotification(n.copy(isRead = true))
            }
        }
    }

    private fun calculateDaysSince(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    val hasNewNotification: StateFlow<Boolean> = notificationDao.getAllNotifications()
        .map { list -> list.any { it.isNew } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun clearNewFlag() {
        viewModelScope.launch {
            notificationDao.clearNewFlag()
        }
    }
}





