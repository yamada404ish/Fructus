package com.example.fructus.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Home

@Serializable
data object Notification

@Serializable
data class Detail (
    val id: Int
)
