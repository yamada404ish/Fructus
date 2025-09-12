package com.example.fructus.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.fructus.data.local.FruitDatabase
import com.example.fructus.ui.archive.ArchiveScreen
import com.example.fructus.ui.archive.ArchiveViewModel
import com.example.fructus.ui.camera.Camera
import com.example.fructus.ui.detail.DetailScreen
import com.example.fructus.ui.home.HomeScreen
import com.example.fructus.ui.notification.NotificationScreen
import com.example.fructus.ui.notification.NotificationViewModel
import com.example.fructus.ui.notification.NotificationViewModelFactory
import com.example.fructus.ui.onboard.OnboardingScreen
import com.example.fructus.ui.setting.SettingsScreen
import com.example.fructus.ui.shared.AppBackgroundScaffold
import com.example.fructus.ui.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FructusNav(
    navController: NavHostController,
    shouldOpenNotifications: Boolean = false,
    targetFruitId: Int? = null,
    targetNotificationId: Int? = null
) {
    // COMPLETELY bypass splash for notifications
    val startDestination = when {
        shouldOpenNotifications && targetFruitId != null ->
            Detail(targetFruitId, targetNotificationId) // ⭐ include notificationId
        shouldOpenNotifications -> Notification
        else -> Splash
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Only add Splash route if NOT coming from notification
        if (!shouldOpenNotifications) {
            composable<Splash> {
                AppBackgroundScaffold {
                    SplashScreen { onboardingCompleted ->
                        val destination = if (onboardingCompleted) Home else OnBoard
                        navController.navigate(destination) {
                            popUpTo(Splash) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }

        addCoreDestinations(
            navController = navController,
            shouldOpenNotifications = shouldOpenNotifications
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun NavGraphBuilder.addCoreDestinations(
    navController: NavHostController,
    shouldOpenNotifications: Boolean
) {
    // Onboarding - only if not from notification
    if (!shouldOpenNotifications) {
        composable<OnBoard> {
            AppBackgroundScaffold {
                OnboardingScreen {
                    navController.navigate(Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    // Home
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
                },
                onSettingsClick = { navController.navigate(Settings) }
            )
        }
    }

    // Detail - ALWAYS available
    composable<Detail> {
        AppBackgroundScaffold {
            val args = it.toRoute<Detail>() // ⭐ now gives you both id + notificationId

            BackHandler {
                if (shouldOpenNotifications) {
                    navController.navigate(Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.navigateUp()
                }
            }


            DetailScreen(
                fruitId = args.id,
                notificationId = args.notificationId, // ⭐ pass notificationId to DetailScreen
                shouldOpenNotifications = shouldOpenNotifications,
                onNavigate = {
                    if (shouldOpenNotifications) {
                        navController.navigate(Home) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            )
        }
    }

    // Notifications
    composable<Notification> {
        AppBackgroundScaffold {
            val context = LocalContext.current
            val db = remember { FruitDatabase.getDatabase(context) }
            val factory = remember {
                NotificationViewModelFactory(
                    db.fruitDao(),
                    db.notificationDao(),
                    context
                )
            }
            val viewModel: NotificationViewModel = viewModel(factory = factory)

            BackHandler {
                if (shouldOpenNotifications) {
                    navController.navigate(Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.navigateUp()
                }
            }

            NotificationScreen(
                viewModel = viewModel,
                onArchiveClick = { navController.navigate(Archive) },
                onNavigateUp = {
                    if (shouldOpenNotifications) {
                        navController.navigate(Home) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigateUp()
                    }
                },
                // ⭐ update: include notificationId when navigating
                onNotificationNavigate = { fruitId, notificationId ->
                    navController.navigate(Detail(fruitId, notificationId))
                }
            )
        }
    }

    // Archive
    composable<Archive> {
        AppBackgroundScaffold {
            val context = LocalContext.current
            val db = remember { FruitDatabase.getDatabase(context) }
            val factory = remember {
                ArchiveViewModel.ArchiveViewModelFactory(db.notificationDao())
            }
            val archiveViewModel: ArchiveViewModel = viewModel(factory = factory)

            val archivedNotifications by archiveViewModel.archivedNotifications.collectAsState()
            ArchiveScreen(
                archivedNotifications = archivedNotifications,
                onRestoreNotification = archiveViewModel::restoreNotification,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }

    // Settings
    composable<Settings> {
        AppBackgroundScaffold {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }

    // Scan
    composable<Scan>(
        enterTransition = { fadeIn(tween(0)) },
        exitTransition = { fadeOut(tween(0)) }
    ) {
        val context = LocalContext.current
        Camera(
            context = context,
            onNavigateUp = {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}
