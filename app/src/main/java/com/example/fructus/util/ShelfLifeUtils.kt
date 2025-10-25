package com.example.fructus.util

import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.camera.model.ShelfLifeRange

fun getShelfLifeRange(fruitName: String, ripeness: String): ShelfLifeRange {
    val name = fruitName.lowercase().trim()
    val stage = ripeness.lowercase().trim()

    if (name.contains("spoiled") || stage == "spoiled") {
        return ShelfLifeRange(-1, -1)
    }

    android.util.Log.d("ShelfLifeCheck", "Fruit=$name, Stage=$stage")


    return when (name) {
        "lakatan", "cavendish" -> when (stage) {
            "unripe" -> ShelfLifeRange(9, 11)
            "ripe" -> ShelfLifeRange(4, 6)
            "overripe" -> ShelfLifeRange(1, 2)
            else -> ShelfLifeRange(3, 5)
        }
        "saba" -> when (stage) {
            "unripe" -> ShelfLifeRange(14, 20)
            "ripe" -> ShelfLifeRange(5, 7)
            "overripe" -> ShelfLifeRange(1, 3)
            else -> ShelfLifeRange(4, 6)
        }
        "tomato" -> when (stage) {
            "unripe" -> ShelfLifeRange(14, 21)
            "ripe" -> ShelfLifeRange(9, 7)
            "overripe" -> ShelfLifeRange(1, 2)
            else -> ShelfLifeRange(3, 5)
        }
        "carabao" -> when (stage) {
            "unripe" -> ShelfLifeRange(11, 13)
            "ripe" -> ShelfLifeRange(3, 7)
            "overripe" -> ShelfLifeRange(1, 3)
            else -> ShelfLifeRange(3, 5)
        }
        else -> ShelfLifeRange(3, 5)
    }
}

fun getDisplayShelfLife(fruit: FruitEntity): String {
    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)

    if (shelfLifeRange.minDays == -1 ) return "---"

    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    return when {
        remainingShelfLife <= 0 -> "Spoiled!"
        remainingShelfLife == 1 -> "Spoiling!"
        else -> "$remainingShelfLife days"
    }
}

fun isFruitSpoiled(fruit: FruitEntity): Boolean {
    val name = fruit.name.lowercase().trim()
    val stage = fruit.ripeningStage.lowercase().trim()

    if (name.contains("spoiled") || stage == "spoiled") return true

    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    return remainingShelfLife <= 0
}
