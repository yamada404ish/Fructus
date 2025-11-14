package com.example.fructus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.data.local.entity.NotificationEntity
import com.example.fructus.util.SecureDatabaseKeyManager
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [FruitEntity::class, NotificationEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FruitDatabase : RoomDatabase() {
    abstract fun fruitDao(): FruitDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        private const val DATABASE_NAME = "fructus_db"
        @Volatile
        private var INSTANCE: FruitDatabase? = null

        fun getDatabase(context: Context): FruitDatabase {
            return INSTANCE ?: synchronized(this) {
                // 🔐 Get the encrypted passphrase
                val passphrase = SecureDatabaseKeyManager.getOrCreatePassphrase(context)
                val factory = SupportFactory(passphrase)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FruitDatabase::class.java,
                    DATABASE_NAME
                )
                    // Use encrypted DB
                    .openHelperFactory(factory)

                    // ❌ No fallback destruction — define migrations properly later
                    // .fallbackToDestructiveMigration()

                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
