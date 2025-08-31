package com.example.fructus.data.mapper

import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.model.Fruit

fun FruitEntity.toModel(): Fruit {
    return Fruit(
        id = id,
        name = name,
        shelfLife = shelfLife,
        ripeningStage = ripeningStage,
        ripeningProcess = ripeningProcess,
        scannedDate = scannedDate
    )
}
