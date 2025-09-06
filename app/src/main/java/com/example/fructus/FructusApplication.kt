package com.example.fructus

import android.app.Application
import com.example.fructus.data.local.FruitDatabase

class FructusApplication : Application() {
    lateinit var database: FruitDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("FructusApplication", "App starting up...")

        database = FruitDatabase.getDatabase(this)

        // Initialize WorkManager and schedule notifications
        val scheduler = com.example.fructus.util.NotificationScheduler(this)
        scheduler.schedulePeriodicNotifications()

        android.util.Log.d("FructusApplication", "Background notifications scheduled")
    }
}