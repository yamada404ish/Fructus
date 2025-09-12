package com.example.fructus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.fructus.ui.FructusApp
import com.example.fructus.util.NotificationSoundUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        // 🔥 Flow that emits navigation requests from notifications
        val notificationNavFlow = MutableSharedFlow<Triple<Boolean, Int?, Int?>>()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        NotificationSoundUtils.checkNotificationSettings(this)
        NotificationSoundUtils.logDeviceAudioSettings(this)

        // initial extras
        val shouldOpenNotifications = intent?.getBooleanExtra("open_notifications", false) ?: false
        val targetFruitId = intent?.getIntExtra("fruit_id", -1)?.takeIf { it != -1 }
        val targetNotificationId = intent?.getIntExtra("notification_id", -1)?.takeIf { it != -1 }

        setContent {
            FructusApp(
                shouldOpenNotifications = shouldOpenNotifications,
                targetFruitId = targetFruitId,
                targetNotificationId = targetNotificationId,
                notificationFlow = notificationNavFlow // ✅ pass flow
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val shouldOpenNotifications = intent.getBooleanExtra("open_notifications", false)
        val targetFruitId = intent.getIntExtra("fruit_id", -1).takeIf { it != -1 }
        val targetNotificationId = intent.getIntExtra("notification_id", -1).takeIf { it != -1 }

        lifecycleScope.launch {
            notificationNavFlow.emit(Triple(shouldOpenNotifications, targetFruitId, targetNotificationId))
        }
    }
}