package com.example.fructus.util

import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.camera.model.ShelfLifeRange

fun getShelfLifeRange(fruitName: String, ripeness: String): ShelfLifeRange {
    val name = fruitName.lowercase()
    val stage = ripeness.lowercase()

    return when (name) {
        "lakatan", "cavendish" -> when (stage) {
            "unripe" -> ShelfLifeRange(7, 10)
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
            "ripe" -> ShelfLifeRange(5, 7)
            "overripe" -> ShelfLifeRange(1, 2)
            else -> ShelfLifeRange(3, 5)
        }
        "carabao" -> when (stage) {
            "unripe" -> ShelfLifeRange(10, 14)
            "ripe" -> ShelfLifeRange(5, 6)
            "overripe" -> ShelfLifeRange(1, 2)
            else -> ShelfLifeRange(3, 5)
        }
        else -> ShelfLifeRange(3, 5)
    }
}

fun getDisplayShelfLife(fruit: FruitEntity): String {
    val isSpoiled = Regex("^spoiled", RegexOption.IGNORE_CASE).containsMatchIn(fruit.name)
    if (isSpoiled) return "---"

    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    return when {
        remainingShelfLife < 0 -> "Spoiled!"
        remainingShelfLife == 0 -> "Expiring!"
        remainingShelfLife == 1 -> "1 day"
        else -> "$remainingShelfLife days"
    }
}

fun isFruitSpoiled(fruit: FruitEntity): Boolean {
    val nameIsSpoiled = Regex("^spoiled", RegexOption.IGNORE_CASE).containsMatchIn(fruit.name)
    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    return nameIsSpoiled || remainingShelfLife < 0
}

