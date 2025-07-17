package com.example.fructus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fructus.ui.screens.home.HomeScreen
import com.example.fructus.ui.screens.notification.NotificationScreen

@Composable
fun FructusNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(navController)
        }
        composable<Notification> {
            NotificationScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}