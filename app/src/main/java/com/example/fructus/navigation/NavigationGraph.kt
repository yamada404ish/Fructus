package com.example.fructus.navigation

import TextScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fructus.ui.detail.DetailScreen
import com.example.fructus.ui.home.HomeScreen
import com.example.fructus.ui.notification.NotificationScreen
import com.example.fructus.ui.notification.NotificationViewModel
import com.example.fructus.ui.onboard.OnboardingScreen
import com.example.fructus.ui.setting.SettingsScreen
import com.example.fructus.ui.shared.AppBackgroundScaffold
import com.example.fructus.ui.splash.SplashScreen
import com.example.fructus.ui.screens.camera.RealTimePredictionScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FructusNav() {
    val navController = rememberNavController()

    AppBackgroundScaffold {
        NavHost(
            navController = navController,
            startDestination = Splash
        ) {
            // Splash screen
            composable<Splash> {
                SplashScreen { onboardingCompleted ->
                    navController.navigate(
                        if (onboardingCompleted) Home else OnBoard
                    ) {
                        popUpTo(Splash) { inclusive = true }
                    }
                }
            }

            // Onboarding screen
            composable<OnBoard> {
                OnboardingScreen {
                    navController.navigate(Home) {
                        popUpTo(OnBoard) { inclusive = true }
                    }
                }
            }

            // Home screen
            composable<Home> {
                HomeScreen(
                    navController = navController,
                    onFruitClick = { id -> navController.navigate(Detail(id)) },
                    onNavigateToScan = { navController.navigate(Scan) } // Added Scan here
                )
            }

            // Detail screen
            composable<Detail> {
                val args = it.toRoute<Detail>()
                DetailScreen(
                    fruitId = args.id,
                    onNavigate = { navController.navigateUp() }
                )
            }

            // Notification screen
            composable<Notification> {
                val viewModel = remember { NotificationViewModel() }
                NotificationScreen(
                    viewModel = viewModel,
                    onNavigateUp = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate(Settings) }
                )
            }

            // Settings screen
            composable<Settings> {
                SettingsScreen(
                    onNavigateUp = { navController.navigateUp() }
                )
            }

            // Scan (Real-time prediction)
            composable<Scan> {
                val context = LocalContext.current
                RealTimePredictionScreen(
                    context = context,
                    navController = navController
                )
            }

            // Extra test screen
            composable<Test> {
                TextScreen()
            }
        }
    }
}
