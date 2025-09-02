package com.example.fructus.util

import com.example.fructus.ui.camera.model.ShelfLifeRange

fun getDisplayFruitName(fruitName: String): String {
    return when (fruitName.lowercase()) {
        "lakatan", "saba", "cavendish" -> "$fruitName Banana"
        "carabao" -> "$fruitName Mango"
        else -> fruitName
    }
}

fun formatShelfLifeRange(range: ShelfLifeRange): String {
    return "${range.minDays}–${range.maxDays} days"
}
