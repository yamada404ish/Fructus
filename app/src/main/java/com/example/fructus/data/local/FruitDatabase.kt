package com.example.fructus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.data.local.entity.NotificationEntity

@Database(
    entities = [FruitEntity::class, NotificationEntity::class],
    version = 3, // ✅ bumped version (2 → 3 to reset schema mismatch)
    exportSchema = false
)
abstract class FruitDatabase : RoomDatabase() {

    abstract fun fruitDao(): FruitDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DATABASE_NAME = "fructus_db"

        @Volatile
        private var INSTANCE: FruitDatabase? = null

        fun getDatabase(context: Context): FruitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FruitDatabase::class.java,
                    DATABASE_NAME
                )
                    // ✅ For development: clears DB when schema changes
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
