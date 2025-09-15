package com.example.fructus.util

import com.example.fructus.ui.camera.model.ShelfLifeRange

fun getDisplayFruitName(fruitName: String): String {
    return when (fruitName.lowercase()) {
        "lakatan", "saba", "cavendish" -> "$fruitName Banana"
        "carabao" -> "$fruitName Mango"
        else -> fruitName
    }
}

fun getFruitDescription(fruitName: String): String {
    return when (fruitName.lowercase()) {
        "tomato" -> "Also called Kamatis, is a staple in Filipino cuisine, valued for its versatility and rich nutritional content."
        "carabao" -> "Also called Manila mango, it is a prized Filipino variety known for its exceptional sweetness, rich aroma, and smooth, fiberless flesh."
        "saba" -> "It is one of the most common banana cultivars in the Philippines."
        "cavendish" -> "It is the most widely grown banana worldwide, known for its mild sweetness and creamy texture when ripe."
        "lakatan" -> "It is a popular Philippine variety known for its good taste and export value."
        else -> "A delicious and healthy fruit."
    }
}

fun formatShelfLifeRange(range: ShelfLifeRange): String {
    return "${range.minDays}–${range.maxDays} days"
}
