package com.example.fructus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fructus.ui.screens.detail.DetailScreen
import com.example.fructus.ui.screens.home.HomeScreen
import com.example.fructus.ui.screens.notification.NotificationScreen
import com.example.fructus.ui.screens.notification.loadNotificationsIfNeeded
import com.example.fructus.ui.screens.notification.model.Filter
import com.example.fructus.ui.screens.notification.notificationList
import com.example.fructus.ui.screens.setting.SettingsScreen
import com.example.fructus.ui.screens.splash.SplashScreen
import com.example.fructus.ui.shared.AppBackgroundScaffold

@Composable
fun FructusNav() {
    val navController = rememberNavController()
    AppBackgroundScaffold {

        NavHost(
            navController = navController,
            startDestination = Splash
        ) {
            composable<Splash> {
                SplashScreen(
                    onAnimationFinished = {
                        navController.navigate(Home) {
                            popUpTo(Splash) { inclusive = true }
                        }
                    }
                )
            }
            composable<Home> {
                HomeScreen(
                    navController = navController
                ){
                    navController.navigate(Detail(it))
                }

            }
            composable<Notification> {
                val (filter, onSelectedFilter) = remember { mutableStateOf(Filter.All) }

                loadNotificationsIfNeeded()
                val notifications = notificationList

                NotificationScreen(
                    notifications = notifications,
                    filter = filter,
                    onSelectedFilter = onSelectedFilter,
                    onNotificationClick = { index ->
                        notificationList[index] = notificationList[index].copy(isRead = true)
                    },
//                    onNotificationClick = { index ->
//                        notifications[index] = notifications[index].copy(isRead = true)
//                    },
                    onMarkAllAsRead = {
                        notifications.forEachIndexed { index, notification ->
                            if (!notification.isRead) {
                                notifications[index] = notification.copy(isRead = true)

                            }
                        }
                    },
                    onNavigateUp = { navController.navigateUp() },
                    onSettingsClick = {
                        navController.navigate(Settings) // <<< Navigate to your Settings destination/route
                    }
                )
            }

            composable<Settings> {
                SettingsScreen(
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable<Detail> {
                val details: Detail = it.toRoute()
                DetailScreen(
                    details.id
                ){
                    navController.navigateUp()
                }
            }
        }

    }

}