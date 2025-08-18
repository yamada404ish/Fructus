package com.example.fructus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fructus.data.local.entity.FruitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FruitDao {

    @Query("SELECT * FROM fruits")
    fun getAllFruits(): Flow<List<FruitEntity>>

    @Query("SELECT * FROM fruits WHERE id = :id")
    fun getFruitById(id: Int): Flow<FruitEntity?>

    @Query("DELETE FROM fruits")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFruit(fruits: FruitEntity)

}