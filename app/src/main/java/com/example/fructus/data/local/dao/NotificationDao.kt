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

    @Query("UPDATE notifications SET isNew = 0")
    suspend fun clearNewFlag()

    @Query("SELECT * FROM notifications WHERE fruitName = :fruitName ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestNotificationForFruit(fruitName: String): NotificationEntity?

//    @Query("UPDATE notifications SET isArchived = 1 WHERE id = :id")
//    suspend fun archiveNotification(id: Int)
//
//    @Query("UPDATE notifications SET isArchived = 0 WHERE id = :id")
//    suspend fun restoreNotification(id: Int)
//
//    @Query("SELECT * FROM notifications WHERE isArchived = 0 ORDER BY timestamp DESC")
//    fun getActiveNotifications(): Flow<List<NotificationEntity>>
//
//    @Query("SELECT * FROM notifications WHERE isArchived = 1 ORDER BY timestamp DESC")
//    fun getArchivedNotifications(): Flow<List<NotificationEntity>>

}

