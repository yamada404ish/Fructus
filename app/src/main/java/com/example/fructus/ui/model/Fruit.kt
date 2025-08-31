package com.example.fructus.ui.model

data class Fruit(
    val id: Int,
    val name: String,
    val shelfLife: Int,
    val ripeningStage: String,
    val ripeningProcess: Boolean,
    val scannedDate: String
)