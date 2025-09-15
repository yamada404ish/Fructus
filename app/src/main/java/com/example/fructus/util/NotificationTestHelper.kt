package com.example.fructus.util

import android.content.Context

class NotificationTestHelper(context: Context) {

    private val pushNotificationManager = PushNotificationManager(context)

    suspend fun testSpoilageNotification() {
        pushNotificationManager.sendFruitSpoilageNotification(
            message = "Apple is spoiled!",
            fruitId = 999
        )
    }

    suspend fun testExpiringNotification() {
        pushNotificationManager.sendFruitSpoilageNotification(
            message = "Banana has only 1 day left!",
            fruitId = 998
        )
    }
}

// Extension function to easily test notifications from anywhere
suspend fun Context.testNotifications() {
    val helper = NotificationTestHelper(this)
    helper.testSpoilageNotification()
    helper.testExpiringNotification()
}