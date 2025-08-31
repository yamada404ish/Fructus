package com.example.fructus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fructus.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()

    @Query("SELECT * FROM notifications WHERE fruitName = :fruitName LIMIT 1")
    suspend fun getNotificationByFruit(fruitName: String): NotificationEntity?


    @Query("SELECT * FROM notifications WHERE fruitName = :name AND scannedDate = :date AND scannedTime = :time LIMIT 1")
    suspend fun getNotificationByFruitAndTimestamp(name: String, date: String, time: String): NotificationEntity?


}

