package com.example.fructus.data.local.model

import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.data.local.entity.NotificationEntity

data class FruitWithNotification(
    val notification: NotificationEntity,
    val fruit: FruitEntity?
)