package com.example.fructus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fructus.ui.components.AppBackgroundScaffold
import com.example.fructus.ui.screens.detail.DetailScreen
import com.example.fructus.ui.screens.home.HomeScreen
import com.example.fructus.ui.screens.notification.NotificationScreen
import com.example.fructus.ui.screens.splash.SplashScreen

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
                NotificationScreen(
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