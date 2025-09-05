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
import java.util.concurrent.TimeUnit

class NotificationViewModel(
    private val fruitDao: FruitDao,
    private val notificationDao: NotificationDao
) : ViewModel() {

    var filter by mutableStateOf(Filter.All)
        private set

    companion object {
        private const val ARCHIVE_AFTER_DAYS = 7L // Notifications stay for 7 days
    }

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications



    init {
        // 1. Auto-archive old notifications periodically
        viewModelScope.launch {
            autoArchiveOldNotifications()
        }

        // 2. Watch fruits and trigger notifications
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
                        remainingShelfLife <= 0 -> "${fruit.name} is spoiled!"
                        else -> null
                    }

                    if (message != null) {
                        // Check if we already notified this exact status
                        val existing = notificationDao.getNotificationByFruitAndTimestamp(
                            fruit.name,
                            fruit.scannedDate,
                            message,
                            fruit.scannedTime,

                        )

                        if (existing == null) {
                            val notification = NotificationEntity(
                                fruitId = fruit.id,
                                fruitName = fruit.name,
                                message = message,
                                isRead = false,
                                isNew = true,
                                scannedDate = fruit.scannedDate,
                                scannedTime = fruit.scannedTime,
                                timestamp = System.currentTimeMillis(),
                                isArchived = false
                            )
                            notificationDao.insertNotification(notification)
                        }
                    }

                }
            }
        }

        // 3. Watch active notifications and enhance messages dynamically
        viewModelScope.launch {
            notificationDao.getActiveNotifications().collect { list ->
                _notifications.value = list.map { enhanceMessage(it) }
            }
        }
    }
    // Auto-archive notifications older than ARCHIVE_AFTER_DAYS
    private suspend fun autoArchiveOldNotifications() {
        val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(ARCHIVE_AFTER_DAYS)
        notificationDao.archiveOldNotifications(cutoffTime)
    }

    // Manual archive function
    fun archiveNotification(notificationId: Int) {
        viewModelScope.launch {
            notificationDao.archiveNotification(notificationId)
        }
    }


    // --- Enhancer for spoiled notifications ---
    private fun enhanceMessage(notification: NotificationEntity): NotificationEntity {
        val daysAgo = calculateDaysSince(notification.timestamp)
        return if (notification.message.contains("spoiled", ignoreCase = true)) {
            val newMessage = when {
                daysAgo in 1..6 -> "${notification.fruitName} has been spoiled for days"
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

    val hasNewNotification: StateFlow<Boolean> = notificationDao.getActiveNotifications()
        .map { list -> list.any { it.isNew } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun clearNewFlag() {
        viewModelScope.launch {
            notificationDao.clearNewFlag()
        }
    }
    // Call this periodically (e.g., when app starts or notification screen opens)
    fun refreshAndArchive() {
        viewModelScope.launch {
            autoArchiveOldNotifications()
        }
    }


}





