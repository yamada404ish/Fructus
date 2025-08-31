package com.example.fructus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val fruitName: String,
    val message: String,
    val isRead: Boolean = false,
    val scannedDate: String,
    val scannedTime: String,


    // store as epoch millis
    val timestamp: Long
)
