package com.example.fructus.util

fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val s = diff / 1000
    val m = s / 60
    val h = m / 60
    val d = h / 24
    val y = d / 365

    return when {
        s < 60 -> "${s}s ago"
        m < 60 -> "${m}m ago"
        h < 24 -> "${h}h ago"
        d < 365 -> "${d}d ago"
        else -> "${y}y ago"
    }
}

fun calculateDaysSince(timestamp: Long): Int {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}
