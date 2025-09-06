package com.example.fructus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.example.fructus.ui.FructusApp
import com.example.fructus.util.NotificationSoundUtils
import com.example.fructus.util.testNotifications

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        testNotifications()

        NotificationSoundUtils.checkNotificationSettings(this)
        NotificationSoundUtils.logDeviceAudioSettings(this)

        setContent {
            FructusApp(
                shouldOpenNotifications = intent?.getBooleanExtra("open_notifications", false) ?: false,
                targetFruitId = intent?.getIntExtra("fruit_id", -1)?.takeIf { it != -1 }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // Handle new notification clicks when app is already running
        val shouldOpenNotifications = intent.getBooleanExtra("open_notifications", false)
        val targetFruitId = intent.getIntExtra("fruit_id", -1).takeIf { it != -1 }

        if (shouldOpenNotifications) {
            // You can use a callback or state management to navigate
            // This is handled in the FructusApp composable
        }
    }
}
