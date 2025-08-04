package com.example.fructus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fructus.ui.detail.DetailScreen
import com.example.fructus.ui.home.HomeScreen
import com.example.fructus.ui.notification.NotificationScreen
import com.example.fructus.ui.notification.NotificationViewModel
import com.example.fructus.ui.setting.SettingsScreen
import com.example.fructus.ui.shared.AppBackgroundScaffold
import com.example.fructus.ui.splash.SplashScreen

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
                ){ id ->
                    navController.navigate(Detail(id))
                }

            }
            composable<Notification> {
                val viewModel = remember { NotificationViewModel() }
                NotificationScreen(
                    viewModel = viewModel,
                    onNavigateUp = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate(Settings) }
                )
            }

            composable<Settings> {
                SettingsScreen(
                    onNavigateUp = { navController.navigateUp() }
                )
            }


            composable<Detail> {
                val args = it.toRoute<Detail>()
                DetailScreen(fruitId = args.id) {
                    navController.navigateUp()
                }
            }
        }

    }
}