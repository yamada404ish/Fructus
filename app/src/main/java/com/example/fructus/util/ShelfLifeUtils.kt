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
        "lakatan", "cavendish", "saba" -> when (stage) {
            "unripe" -> ShelfLifeRange(18, 21)     // 3 weeks
            "ripe" -> ShelfLifeRange(6, 8)         // ~1 week
            "overripe" -> ShelfLifeRange(4, 6)     // <1 week
            else -> ShelfLifeRange(6, 8)
        }

        "carabao" -> when (stage) {
            "unripe" -> ShelfLifeRange(18, 21)     // 3 weeks
            "ripe" -> ShelfLifeRange(10, 14)       // 1.5–2 weeks
            "overripe" -> ShelfLifeRange(6, 8)     // ~1 week
            else -> ShelfLifeRange(6, 8)
        }

        "tomato" -> when (stage) {
            "unripe" -> ShelfLifeRange(7, 10)      // 1–1.5 weeks
            "ripe" -> ShelfLifeRange(6, 8)         // ~1 week
            "overripe" -> ShelfLifeRange(2, 4)     // 2–4 days
            else -> ShelfLifeRange(6, 8)
        }

        else -> ShelfLifeRange(6, 8) // Default range if unknown fruit
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
