package com.example.fructus.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fructus.MainActivity

class PushNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "fruit_spoilage_channel"
        private const val CHANNEL_NAME = "Fruit Spoilage Alerts"
        private const val CHANNEL_DESCRIPTION =
            "Notifications for fruit spoilage and shelf life alerts"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    /** Create channel with sound + vibration */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // ⚠️ Delete old channel first (only in dev / testing builds)
            manager.deleteNotificationChannel(CHANNEL_ID)

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)

                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }

            manager.createNotificationChannel(channel)
        }
    }


    /** Send a fruit spoilage notification */
    fun sendFruitSpoilageNotification(
        fruitName: String,
        message: String,
        fruitId: Int,
        notificationId: Int = NOTIFICATION_ID_BASE + fruitId
    ) {
        if (!areNotificationsEnabled()) {
            android.util.Log.w("PushNotification", "Notifications are disabled by user")
            return
        }

        // Intent to open app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fruit_id", fruitId)
            putExtra("open_notifications", true)
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Pick icon + title
        val (icon, title) = when {
            message.contains("spoiled", ignoreCase = true) ->
                Pair(android.R.drawable.stat_notify_error, "Fruit Spoiled!")
            message.contains("1 day left", ignoreCase = true) ->
                Pair(android.R.drawable.stat_sys_warning, "Fruit Alert!")
            else -> Pair(android.R.drawable.stat_notify_sync, "Fruit Update")
        }

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // ✅ enables sound + vibration
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .build()


        try {
            notificationManager.notify(notificationId, notification)
            android.util.Log.d("PushNotification", "Notification sent: $message")
        } catch (e: SecurityException) {
            android.util.Log.e("PushNotification", "Permission not granted: ${e.message}")
        } catch (e: Exception) {
            android.util.Log.e("PushNotification", "Failed to send notification: ${e.message}")
        }
    }

    /** Check notification permission */
    private fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /** Cancel single notification */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
        android.util.Log.d("PushNotification", "Cancelled notification: $notificationId")
    }

    /** Cancel all notifications */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
        android.util.Log.d("PushNotification", "Cancelled all notifications")
    }

    /** Send test notification */
    fun sendTestNotification() {

        sendFruitSpoilageNotification(
            fruitName = "Test Fruit",
            message = "This is a test notification with sound + vibration",
            fruitId = 99999
        )
    }
}
