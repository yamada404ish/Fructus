package com.example.fructus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "fruits")
data class FruitEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val name: String,
    val shelfLife: Int,
    val ripeningStage: String,
    val ripeningProcess: Boolean,

//    val image: Int
)