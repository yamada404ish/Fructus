package com.example.fructus.data

import com.example.fructus.R

data class Recipe(
    val name: String,
    val description: String,
    val imageRes: Int,
    val type: String
)

object FoodData {
    val foods = listOf(
        Recipe (
            name = "Maruya",
            description = "Fried concoction made from ripe bananas or plantains.",
            imageRes = R.drawable.lakatan,
            type = "lakatan"
        ),
        Recipe (
            name = "Banana Pudding",
            description = "sweet bread or cake made from mashed bananas.",
            imageRes = R.drawable.mango,
            type = "lakatan"
        )
    )
}