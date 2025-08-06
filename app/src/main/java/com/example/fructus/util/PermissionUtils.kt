package com.example.fructus.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

// Function to check if notification permission is granted
fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // For Android 13 (API 33) and above, check runtime permission for notifications
        context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    } else {
        // For Android 12 and below, check if notifications are enabled at system level
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.areNotificationsEnabled()
    }
}

// Function to navigate the user to this app's notification settings screen
fun navigateToNotificationSettings(context: Context) {
    val intent = Intent().apply {
        // Opens the app-specific settings page
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context.packageName, null) // Target the current app
    }
    context.startActivity(intent) // Launch the settings intent
}
