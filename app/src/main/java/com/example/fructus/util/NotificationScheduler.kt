package com.example.fructus.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        private const val WORK_NAME = "fruit_check_work"
        private const val REPEAT_INTERVAL_HOURS = 1L // Change to 1 hour for better reliability
    }

    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodicNotifications() {
        // Create constraints to ensure work runs reliably
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // No network needed
            .setRequiresBatteryNotLow(false) // Allow when battery is low
            .setRequiresCharging(false) // Allow when not charging
            .setRequiresDeviceIdle(false) // Allow when device is not idle
            .build()

        val workRequest = PeriodicWorkRequestBuilder<FruitCheckWorker>(
            REPEAT_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setInitialDelay(30, TimeUnit.SECONDS) // Start after 30 seconds
            .setConstraints(constraints)
            .build()

        android.util.Log.d("NotificationScheduler", "Scheduling periodic work every $REPEAT_INTERVAL_HOURS hour(s)")

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE, // Replace existing work
            workRequest
        )
    }

    fun cancelNotifications() {
        workManager.cancelUniqueWork(WORK_NAME)
        android.util.Log.d("NotificationScheduler", "Cancelled periodic notifications")
    }

    fun scheduleOneTimeCheck() {
        // Cancel current ongoing work first
        WorkManager.getInstance(context).cancelUniqueWork("fruit_check_work")

        val oneTimeRequest = OneTimeWorkRequestBuilder<FruitCheckWorker>()
            .setInitialDelay(2, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("fruit_check_work", ExistingWorkPolicy.REPLACE, oneTimeRequest)

        android.util.Log.d("NotificationScheduler", "Scheduled one-time fruit check (unique)")
    }


    fun getWorkStatus() {
        // Debug method to check work status
        val workInfos = workManager.getWorkInfosForUniqueWork(WORK_NAME)
        workInfos.addListener({
            try {
                val workInfo = workInfos.get()
                android.util.Log.d("NotificationScheduler", "Work status: ${workInfo.map { it.state }}")
            } catch (e: Exception) {
                android.util.Log.e("NotificationScheduler", "Error checking work status", e)
            }
        }, { it.run() })
    }
}