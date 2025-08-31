package com.example.fructus.util

import com.example.fructus.R


fun getDetailBackgroundRes(fruitName: String): Int {
    return when (fruitName.lowercase()) {
        "lakatan" -> R.drawable.detail_lakatan
        "carabao" -> R.drawable.detail_carabao
        "saba" -> R.drawable.detail_saba
        "cavendish" -> R.drawable.detail_cavendish
        "tomato" -> R.drawable.detail_tomato
        else -> R.drawable.unknown_fruit // fallback image
    }
}