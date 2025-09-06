package com.example.fructus.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.fructus.util.NotificationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                Log.d("BootReceiver", "Device booted or app updated - rescheduling notifications")

                // Reschedule periodic notifications
                val scheduler = NotificationScheduler(context)
                scheduler.schedulePeriodicNotifications()

                Log.d("BootReceiver", "Notifications rescheduled after boot/update")
            }
        }
    }
}