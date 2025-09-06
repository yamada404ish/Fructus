package com.example.fructus.util

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

object NotificationSoundUtils {

    fun checkNotificationSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkChannelSettings(context)
        }
        checkSystemSettings(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkChannelSettings(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel("fruit_spoilage_channel")

        if (channel != null) {
            android.util.Log.d("NotificationSound", "Channel importance: ${channel.importance}")
            android.util.Log.d("NotificationSound", "Channel sound: ${channel.sound}")
            android.util.Log.d("NotificationSound", "Channel vibration: ${channel.shouldVibrate()}")
            android.util.Log.d("NotificationSound", "Channel lights: ${channel.shouldShowLights()}")

            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                android.util.Log.w("NotificationSound", "Channel is disabled!")
            }

            if (channel.sound == null) {
                android.util.Log.w("NotificationSound", "Channel sound is disabled!")
            }
        } else {
            android.util.Log.e("NotificationSound", "Notification channel not found!")
        }
    }

    private fun checkSystemSettings(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        android.util.Log.d("NotificationSound", "Notifications enabled: ${notificationManager.areNotificationsEnabled()}")

        android.util.Log.d("NotificationSound", "DND filter: ${notificationManager.currentInterruptionFilter}")
    }

    fun logDeviceAudioSettings(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

        android.util.Log.d("NotificationSound", "Ringer mode: ${audioManager.ringerMode}")
        android.util.Log.d("NotificationSound", "Notification volume: ${audioManager.getStreamVolume(android.media.AudioManager.STREAM_NOTIFICATION)}")
        android.util.Log.d("NotificationSound", "Max notification volume: ${audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_NOTIFICATION)}")
    }
}