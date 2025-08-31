package com.example.fructus.util

fun getDisplayFruitName(fruitName: String): String {
    return if (fruitName.equals("lakatan", ignoreCase = true) ||
        fruitName.equals("saba", ignoreCase = true) ||
        fruitName.equals("cavendish", ignoreCase = true)
    ) {
        "$fruitName Banana"
    } else {
        fruitName
    }
}