package com.example.fructus.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object OnBoard

@Serializable
object Home

@Serializable
data object Notification

@Serializable
data object Settings

@Serializable
data class Detail (
    val id: Int
)

@Serializable
object Test