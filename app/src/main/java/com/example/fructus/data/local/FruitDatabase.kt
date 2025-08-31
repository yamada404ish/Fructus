package com.example.fructus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.data.local.entity.NotificationEntity

@Database(entities = [ FruitEntity::class, NotificationEntity::class ], version = 2, exportSchema = false) // ⬅️ bumped
// version
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
                    // Development only: wipes DB when schema changes
                    .fallbackToDestructiveMigration()

                    // For production later:
                    // .addMigrations(MIGRATION_1_2, MIGRATION_2_3, ...)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
