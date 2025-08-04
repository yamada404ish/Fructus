package com.example.fructus

import android.app.Application
import com.example.fructus.data.local.FruitDatabase

class FructusApplication : Application() {
    lateinit var database: FruitDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = FruitDatabase.getDatabase(this)
    }
}