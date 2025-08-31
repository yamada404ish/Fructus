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
import kotlinx.coroutines.flow.StateFlow
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

                    if (remainingShelfLife == 1) {
                        val existing = notificationDao.getNotificationByFruitAndTimestamp(
                            fruit.name,
                            fruit.scannedDate,
                            fruit.scannedTime
                        )
                        if (existing == null) {
                            val notification = NotificationEntity(
                                fruitName = fruit.name,
                                message = "${fruit.name} has only 1 day left!",
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

        // 2. Watch notifications
        viewModelScope.launch {
            notificationDao.getAllNotifications().collect { list ->
                _notifications.value = list
            }
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

}


