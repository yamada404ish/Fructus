package com.example.fructus.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fructus.data.local.FruitDatabase
import com.example.fructus.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.first

class FruitCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = FruitDatabase.getDatabase(context)
    private val fruitDao = database.fruitDao()
    private val notificationDao = database.notificationDao()
    private val pushNotificationManager = PushNotificationManager(context)

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d("FruitCheckWorker", "=== Background worker started ===")
            android.util.Log.d("FruitCheckWorker", "Time: ${System.currentTimeMillis()}")

            // Check if notifications are enabled
            if (!isNotificationPermissionGranted(applicationContext)) {
                android.util.Log.w("FruitCheckWorker", "Notification permission not granted - skipping push notifications")
            }

            checkFruitsAndNotify()

            android.util.Log.d("FruitCheckWorker", "=== Background worker completed successfully ===")
            Result.success()
        } catch (exception: Exception) {
            android.util.Log.e("FruitCheckWorker", "Error in background worker", exception)
            Result.failure()
        }
    }

    private suspend fun checkFruitsAndNotify() {
        val fruits = fruitDao.getAllFruits().first()
        android.util.Log.d("FruitCheckWorker", "Checking ${fruits.size} fruits")

        if (fruits.isEmpty()) {
            android.util.Log.d("FruitCheckWorker", "No fruits to check")
            return
        }

        fruits.forEach { fruit ->
            android.util.Log.d("FruitCheckWorker", "Checking fruit: ${fruit.name} (ID: ${fruit.id})")

            val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
            val estimatedShelfLife = shelfLifeRange.minDays
            val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
            val remainingShelfLife = estimatedShelfLife - daysSinceScan

            android.util.Log.d("FruitCheckWorker",
                "Fruit: ${fruit.name}, Days since scan: $daysSinceScan, " +
                        "Estimated shelf life: $estimatedShelfLife, Remaining: $remainingShelfLife")

            val message: String? = when {
                remainingShelfLife > 1 -> {
                    android.util.Log.d("FruitCheckWorker", "${fruit.name} is still fresh ($remainingShelfLife days left)")
                    null
                }
                remainingShelfLife == 1 -> "${fruit.name} has only 1 day left!"
                remainingShelfLife <= 0 -> "${fruit.name} is spoiled!"
                else -> null
            }

            if (message != null) {
                android.util.Log.d("FruitCheckWorker", "Need to notify: $message")

                // Check if we already notified this exact status
                val existing = notificationDao.getNotificationByFruitAndTimestamp(
                    fruit.name,
                    fruit.scannedDate,
                    message,
                    fruit.scannedTime
                )

                if (existing == null) {
                    android.util.Log.d("FruitCheckWorker", "Sending new notification: $message")

                    // Send push notification
                    pushNotificationManager.sendFruitSpoilageNotification(
                        fruitName = fruit.name,
                        message = message,
                        fruitId = fruit.id
                    )

                    // Also save to local database
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

                    android.util.Log.d("FruitCheckWorker", "Notification saved to database")
                } else {
                    android.util.Log.d("FruitCheckWorker", "Notification already exists for this status")
                }
            }
        }
    }

    private fun calculateDaysSince(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}