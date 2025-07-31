package com.example.fructus.util

import com.example.fructus.ui.shared.RipenessStage

fun String.toRipenessStage(): RipenessStage {
    return when (this.trim().lowercase()) {
        "unripe" -> RipenessStage.UNRIPE
        "ripe" -> RipenessStage.RIPE
        "overripe" -> RipenessStage.OVERRIPE
        "spoiled" -> RipenessStage.SPOILED
        else -> RipenessStage.UNRIPE // default or handle error
    }
}