package com.example.fructus.data

import androidx.annotation.DrawableRes

data class Fruit (
    val id: Int,

    val name: String,
    val shelfLife: Int,
    val ripeningStage: String,
    val ripeningProcess: Boolean,

    @DrawableRes val image: Int
)

