package com.example.fructus.ui.notification

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.data.local.entity.NotificationEntity
import com.example.fructus.data.local.model.FruitWithNotification
import com.example.fructus.ui.shared.model.Filter
import com.example.fructus.util.NotificationScheduler
import com.example.fructus.util.PushNotificationManager
import com.example.fructus.util.getShelfLifeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationViewModel(
    private val fruitDao: FruitDao,
    private val notificationDao: NotificationDao,
    context: Context
) : ViewModel() {

    var filter by mutableStateOf(Filter.All)
        private set

    companion object {
        private const val ARCHIVE_AFTER_DAYS = 7L
        private const val DELETE_ARCHIVED_AFTER_DAYS = 30L
    }

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications

    // Initialize push notification manager and scheduler
    private val pushNotificationManager = PushNotificationManager(context)
    private val notificationScheduler = NotificationScheduler(context)

    private val _notificationsWithFruit = MutableStateFlow<List<FruitWithNotification>>(emptyList())
    val notificationsWithFruit: StateFlow<List<FruitWithNotification>> = _notificationsWithFruit
    init {
        // Schedule background notifications
        notificationScheduler.schedulePeriodicNotifications()

        // 1. Auto-archive old notifications periodically
        viewModelScope.launch {
            autoArchiveOldNotifications()
        }

        // 2. Watch fruits and trigger notifications (both in-app and push)
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
                            fruit.scannedTime
                        )

                        if (existing == null) {
                            // First save to database to get the notification ID
                            val notification = NotificationEntity(
                                fruitId = fruit.id,
                                fruitName = fruit.name,
                                message = message,
                                isRead = false,
                                isNew = true,
                                scannedDate = fruit.scannedDate,
                                scannedTime = fruit.scannedTime,
                                timestamp = System.currentTimeMillis(),
                                isArchived = false,
                                imagePath = fruit.imagePath
                            )

                            // Insert and get the ID
                            notificationDao.insertNotification(notification)

                            // Get the inserted notification to get its actual ID
                            val insertedNotification = notificationDao.getNotificationByFruitAndTimestamp(
                                fruit.name,
                                fruit.scannedDate,
                                message,
                                fruit.scannedTime,


                            )

                            // Send push notification with the actual notification ID
                            insertedNotification?.let {
                                pushNotificationManager.sendFruitSpoilageNotification(
                                    message = message,
                                    fruitId = fruit.id,
                                    actualNotificationId = it.id // Pass the actual database notification ID
                                )
                            }
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

        viewModelScope.launch {
            combine(
                notificationDao.getActiveNotifications(),
                fruitDao.getAllFruits()
            ) { notifications, fruits ->
                notifications.map { notif ->
                    val matchingFruit = fruits.find { it.id == notif.fruitId }
                    FruitWithNotification(notif, matchingFruit)
                }
            }.collect { combinedList ->
                _notificationsWithFruit.value = combinedList
            }
        }
    }

    // Auto-archive notifications older than ARCHIVE_AFTER_DAYS
    private suspend fun autoArchiveOldNotifications() {
        val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(ARCHIVE_AFTER_DAYS)
        notificationDao.archiveOldNotifications(cutoffTime)

        val deleteCutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(DELETE_ARCHIVED_AFTER_DAYS)
        notificationDao.deleteOldArchivedNotifications(deleteCutoffTime)
    }

    fun archiveNotification(notificationId: Int) {
        viewModelScope.launch {
            notificationDao.archiveNotification(notificationId)
        }
    }

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
            val notification = notificationDao.getNotificationById(notificationId)
            notification?.let {
                if (!it.isRead) {
                    notificationDao.updateNotification(it.copy(isRead = true))
                }
            }
            // ✅ hasNewNotification updates automatically from Flow
        }
    }



    fun markAllAsRead() {
        viewModelScope.launch {
            notificationDao.markAllAsRead()
            // ✅ hasNewNotification becomes false automatically
        }
    }


    private fun calculateDaysSince(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    val hasNewNotification: StateFlow<Boolean> = notificationDao.getActiveNotifications()
        .map { list -> list.any { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun clearNewFlag() {
        viewModelScope.launch {
            notificationDao.clearNewFlag()
        }
    }

    fun refreshAndArchive() {
        viewModelScope.launch {
            autoArchiveOldNotifications()
        }
    }

    val archivedCount: StateFlow<Int> = notificationDao.getArchivedNotifications()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // New functions for push notifications
    fun triggerImmediateCheck() {
        notificationScheduler.scheduleOneTimeCheck()
    }

    fun cancelPushNotification(notificationId: Int) {
        pushNotificationManager.cancelNotification(notificationId)
    }

    fun cancelAllPushNotifications() {
        pushNotificationManager.cancelAllNotifications()
    }
}