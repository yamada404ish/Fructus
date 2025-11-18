package com.example.fructus.util

import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.camera.model.ShelfLifeRange

fun getShelfLifeRange(fruitName: String, ripeness: String, isRipeningProcess: Boolean): ShelfLifeRange {
    val name = fruitName.lowercase().trim()
    val stage = ripeness.lowercase().trim()

    if (name.contains("spoiled") || stage == "spoiled") {
        return ShelfLifeRange(-1, -1)
    }

    android.util.Log.d("ShelfLifeCheck", "Fruit=$name, Stage=$stage")

    val range = when (name) {
        "lakatan", "cavendish", "saba" -> when (stage) {
            "unripe" -> ShelfLifeRange(18, 21)
            "ripe" -> ShelfLifeRange(6, 8)
            "overripe" -> ShelfLifeRange(4, 6)
            else -> ShelfLifeRange(6, 8)
        }

        "carabao" -> when (stage) {
            "unripe" -> ShelfLifeRange(13, 15)
            "ripe" -> ShelfLifeRange(5, 8)
            "overripe" -> ShelfLifeRange(4, 7)
            else -> ShelfLifeRange(6, 8)
        }

        "tomato" -> when (stage) {
            "unripe" -> ShelfLifeRange(7, 10)
            "ripe" -> ShelfLifeRange(6, 8)
            "overripe" -> ShelfLifeRange(2, 4)
            else -> ShelfLifeRange(6, 8)
        }

        else -> ShelfLifeRange(6, 8)
    }

    // Subtract 2 days IF fruit is Carabao AND the ripening process is false
    return if (name == "carabao" && !isRipeningProcess) {
        ShelfLifeRange(
            (range.minDays - 2).coerceAtLeast(1), // avoid 0 or negative
            (range.maxDays - 2).coerceAtLeast(range.minDays - 2)
        )
    } else {
        range
    }
}

fun getDisplayShelfLife(fruit: FruitEntity): String {
    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage, fruit.ripeningProcess)

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

    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage, fruit.ripeningProcess)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    return remainingShelfLife <= 0
}
