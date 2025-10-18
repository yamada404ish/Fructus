package com.example.fructus.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object OnBoard

@Serializable
object Home

@Serializable
object Guide

@Serializable
object Notification

@Serializable
object Archive

@Serializable
object Scan

@Serializable
object Settings

@Serializable
object OnBoardPreview

@Serializable
data class Detail (
    val id: Int,
    val notificationId: Int? = null,
    val fromNotifications: Boolean = false
)

//@Serializable
//object Test