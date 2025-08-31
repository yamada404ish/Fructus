package com.example.fructus.data

import com.example.fructus.ui.model.Fruit   // ✅ use UI model

data class FruitNotification(
    val fruit: Fruit,
    val isRead: Boolean
)
