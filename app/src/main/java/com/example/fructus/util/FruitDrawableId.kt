package com.example.fructus.util

import com.example.fructus.R

fun getFruitDrawableId(fruitName: String): Int {
    return when (fruitName.lowercase()) {
        "tomato" -> R.drawable.tomato_ph
        "cavendish" -> R.drawable.cavendish_ph
        "lakatan" -> R.drawable.lakatan_ph
        "saba" -> R.drawable.saba_ph
        "carabao" -> R.drawable.carabao_ph
        else -> R.drawable.tomato_ph
    }
}