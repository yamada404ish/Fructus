package com.example.fructus.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fructus.ui.camera.RealTimePredictionScreen
import com.example.fructus.ui.detail.DetailScreen
import com.example.fructus.ui.home.HomeScreen
import com.example.fructus.ui.notification.NotificationScreen
import com.example.fructus.ui.notification.NotificationViewModel
import com.example.fructus.ui.onboard.OnboardingScreen
import com.example.fructus.ui.setting.SettingsScreen
import com.example.fructus.ui.shared.AppBackgroundScaffold
import com.example.fructus.ui.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FructusNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        // Splash screen
        composable<Splash> {
            AppBackgroundScaffold {
                SplashScreen { onboardingCompleted ->
                    navController.navigate(
                        if (onboardingCompleted) Home else OnBoard
                    ) {
                        popUpTo(Splash) { inclusive = true }
                    }
                }
            }
        }

        // Onboarding screen
        composable<OnBoard> {
            AppBackgroundScaffold {
                OnboardingScreen {
                    navController.navigate(Home) {
                        popUpTo(OnBoard) { inclusive = true }
                    }
                }
            }
        }

        // Home screen
        composable<Home> {
            AppBackgroundScaffold {
                HomeScreen(
                    navController = navController,
                    onFruitClick = { id -> navController.navigate(Detail(id)) },
                    onNavigateToScan = {
                        navController.navigate(Scan) {
                            launchSingleTop = true
                            popUpTo(Home) { inclusive = false }
                        }
                    }
                )
            }
        }

        // Detail screen
        composable<Detail> {
            AppBackgroundScaffold {
                val args = it.toRoute<Detail>()
                DetailScreen(
                    fruitId = args.id,
                    onNavigate = { navController.navigateUp() }
                )
            }
        }

        // Notification screen
        composable<Notification> {
            AppBackgroundScaffold {
                val viewModel = remember { NotificationViewModel() }
                NotificationScreen(
                    viewModel = viewModel,
                    onNavigateUp = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate(Settings) }
                )
            }
        }

        // Settings screen
        composable<Settings> {
            AppBackgroundScaffold {
                SettingsScreen(
                    onNavigateUp = { navController.navigateUp() }
                )
            }
        }

        // Scan (Real-time prediction) - NO AppBackgroundScaffold here!
        composable<Scan> (
            enterTransition = { fadeIn(tween(0)) }, // Instant transition
            exitTransition = { fadeOut(tween(0)) }   // Instant transition
        ) {
            val context = LocalContext.current
            RealTimePredictionScreen(
                context = context,
                onNavigateUp = {
                    navController.navigate(Home) {
                        popUpTo<Home> { inclusive = true }
                    }
                }
            )
        }
    }
}