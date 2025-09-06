package com.example.fructus.util

import android.content.Context

class NotificationTestHelper(context: Context) {

    private val pushNotificationManager = PushNotificationManager(context)

    fun testSpoilageNotification() {
        pushNotificationManager.sendFruitSpoilageNotification(
            fruitName = "Apple",
            message = "Apple is spoiled!",
            fruitId = 999
        )
    }

    fun testExpiringNotification() {
        pushNotificationManager.sendFruitSpoilageNotification(
            fruitName = "Banana",
            message = "Banana has only 1 day left!",
            fruitId = 998
        )
    }
}

// Extension function to easily test notifications from anywhere
fun Context.testNotifications() {
    val helper = NotificationTestHelper(this)
    helper.testSpoilageNotification()
    helper.testExpiringNotification()
}