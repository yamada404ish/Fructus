package com.example.fructus.data

data class Recipe(
    val name: String,
    val description: String,
    val imageResName: String,
    val fruitType: String,
    val stage: String,
    val ingredients: List<String>,
    val directions: List<String>,
)