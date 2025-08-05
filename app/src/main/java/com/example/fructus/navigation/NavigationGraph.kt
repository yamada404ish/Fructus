package com.example.fructus.navigation

import SettingsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fructus.ui.detail.DetailScreen
import com.example.fructus.ui.home.HomeScreen
import com.example.fructus.ui.notification.NotificationScreen
import com.example.fructus.ui.notification.NotificationViewModel
import com.example.fructus.ui.onboard.OnboardingCarousel
import com.example.fructus.ui.onboard.OnboardingViewModel
import com.example.fructus.ui.onboard.OnboardingViewModelFactory
import com.example.fructus.ui.shared.AppBackgroundScaffold
import com.example.fructus.ui.shared.RequestNotificationPermission
import com.example.fructus.ui.splash.SplashScreen
import com.example.fructus.util.DataStoreManager

@Composable
fun FructusNav() {
    val navController = rememberNavController()
    AppBackgroundScaffold {

        NavHost(
            navController = navController,
            startDestination = Splash
        ) {
            composable<Splash> {
                val context = LocalContext.current
                val dataStore = remember { DataStoreManager(context) }

                val viewModel: OnboardingViewModel = viewModel(
                    factory = OnboardingViewModelFactory(dataStore)
                )

                val onboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

                SplashScreen(
                    onAnimationFinished = {
                        navController.navigate(
                            if (onboardingCompleted) Home else OnBoard
                        ) {
                            popUpTo(Splash) { inclusive = true }
                        }
                    }
                )
            }
            composable<OnBoard> {
                val context = LocalContext.current
                val dataStore = remember { DataStoreManager(context) }

                val viewModel: OnboardingViewModel = viewModel(
                    factory = OnboardingViewModelFactory(dataStore)
                )

                OnboardingCarousel(
                    onGetStarted = {
                        navController.navigate(Home) {
                            popUpTo(OnBoard) { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable<Home> {

                val showPermissionDialog = remember { mutableStateOf(false) }

                if (showPermissionDialog.value) {
                    RequestNotificationPermission(
                        onGranted = {
                            showPermissionDialog.value = false
                            navController.navigate(Home) {
                                popUpTo(OnBoard) { inclusive = true }
                            }
                        },
                        onDenied = {
                            showPermissionDialog.value = false
                            navController.navigate(Home) {
                                popUpTo(OnBoard) { inclusive = true }
                            }
                        }
                    )
                }
                HomeScreen(
                    navController = navController
                ){ id ->
                    showPermissionDialog.value = true
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