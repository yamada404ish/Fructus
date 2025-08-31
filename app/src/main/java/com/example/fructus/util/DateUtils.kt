package com.example.fructus.util

fun isYesterday(timestamp: Long): Boolean {
    val now = System.currentTimeMillis()
    val oneDayMillis = 1000 * 60 * 60 * 24
    val todayStart = now - (now % oneDayMillis)
    val yesterdayStart = todayStart - oneDayMillis
    return timestamp in yesterdayStart until todayStart
}

fun isToday(timestamp: Long): Boolean {
    val now = System.currentTimeMillis()
    val oneDayMillis = 1000 * 60 * 60 * 24
    val todayStart = now - (now % oneDayMillis)
    return timestamp >= todayStart
}