package com.example.fructus.ui.home

import com.example.fructus.data.local.entity.FruitEntity

data class HomeState(
    val fruits: List<FruitEntity> = emptyList()
)
