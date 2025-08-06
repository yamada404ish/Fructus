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
import com.example.fructus.ui.onboard.OnboardingScreen
import com.example.fructus.ui.setting.SettingsScreen
import com.example.fructus.ui.shared.AppBackgroundScaffold
import com.example.fructus.ui.splash.SplashScreen

@Composable
fun FructusNav() {
    // Create and remember the NavController to manage navigation between screens
    val navController = rememberNavController()

    AppBackgroundScaffold {

        // Define the navigation host with the starting screen (Splash)
        NavHost(
            navController = navController,
            startDestination = Splash // The very first screen shown when the app opens
        ) {

            // Splash screen composable
            composable<Splash> {
                SplashScreen(
                    // Callback called after the animation finishes
                    onAnimationFinished = { onboardingCompleted ->
                        // Navigate to either Home or Onboarding screen based on user state
                        navController.navigate(
                            if (onboardingCompleted) Home else OnBoard
                        ) {
                            // Remove Splash from backstack so user can't return to it
                            popUpTo(Splash) { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding screen composable
            composable<OnBoard> {
                OnboardingScreen(
                    // After "Get Started", go to Home and remove Onboarding from backstack
                    onGetStarted = {
                        navController.navigate(Home) {
                            popUpTo(OnBoard) { inclusive = true }
                        }
                    }
                )
            }

            // Home screen composable
            composable<Home> {
                HomeScreen(
                    navController = navController,
                    // When fruit is clicked, navigate to Detail screen with its ID
                    onFruitClick = { id -> navController.navigate(Detail(id)) }
                )
            }

            // Detail screen composable with arguments
            composable<Detail> {
                val args = it.toRoute<Detail>() // Get the ID passed from Home
                DetailScreen(
                    fruitId = args.id,
                    onNavigate = { navController.navigateUp() } // Go back when needed
                )
            }

            // Notification screen composable
            composable<Notification> {
                // Create a NotificationViewModel instance
                val viewModel = remember { NotificationViewModel() }
                NotificationScreen(
                    viewModel = viewModel,
                    onNavigateUp = { navController.navigateUp() }, // Back button behavior
                    onSettingsClick = { navController.navigate(Settings) } // Go to settings
                )
            }

            // Settings screen composable
            composable<Settings> {
                SettingsScreen(
                    onNavigateUp = { navController.navigateUp() } // Back to previous screen
                )
            }
        }
    }
}