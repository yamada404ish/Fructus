package com.example.fructus.util

import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.camera.model.ShelfLifeRange

fun getShelfLifeRange(fruitName: String, ripeness: String): ShelfLifeRange {
    val name = fruitName.lowercase().trim()
    val stage = ripeness.lowercase().trim()

    // ✅ handle spoiled either by name or stage
    if (name.contains("spoiled") || stage == "spoiled") {
        return ShelfLifeRange(-1, -1)
    }

    android.util.Log.d("ShelfLifeCheck", "Fruit=$name, Stage=$stage")


    return when (name) {
        "lakatan", "cavendish" -> when (stage) {
            "unripe" -> ShelfLifeRange(7, 10)
            "ripe" -> ShelfLifeRange(4, 6)
            "overripe" -> ShelfLifeRange(1, 2)
            else -> ShelfLifeRange(3, 5) // fallback
        }
        "saba" -> when (stage) {
            "unripe" -> ShelfLifeRange(14, 20)
            "ripe" -> ShelfLifeRange(5, 7)
            "overripe" -> ShelfLifeRange(1, 3)
            else -> ShelfLifeRange(4, 6)
        }
        "tomato" -> when (stage) {
            "unripe" -> ShelfLifeRange(14, 21) // ✅ fixed unripe tomato
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
        else -> ShelfLifeRange(3, 5) // ✅ default fallback for unknown fruits
    }
}

fun getDisplayShelfLife(fruit: FruitEntity): String {
    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)

    // ✅ Spoiled case → show "---"
    if (shelfLifeRange.minDays == -1) return "---"

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

    // ✅ check spoiled by both name & stage
    if (name.contains("spoiled") || stage == "spoiled") return true

    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    return remainingShelfLife < 0
}
