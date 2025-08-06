package com.example.fructus.util

import com.example.fructus.ui.shared.RipenessStage

// Extension function to convert a String into a RipenessStage enum
fun String.toRipenessStage(): RipenessStage {
    return when (this.trim().lowercase()) {
        "unripe" -> RipenessStage.UNRIPE
        "ripe" -> RipenessStage.RIPE
        "overripe" -> RipenessStage.OVERRIPE
        "spoiled" -> RipenessStage.SPOILED
        else -> RipenessStage.UNRIPE
    }
}