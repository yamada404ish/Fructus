package com.example.fructus.navigation

import android.app.Activity
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
import com.example.fructus.ui.detail.components.RecipeInformation
import com.example.fructus.ui.guide.UserGuide
import com.example.fructus.ui.home.HomeScreen
import com.example.fructus.ui.notification.NotificationScreen
import com.example.fructus.ui.notification.NotificationViewModel
import com.example.fructus.ui.notification.NotificationViewModelFactory
import com.example.fructus.ui.onboard.OnboardingScreen
import com.example.fructus.ui.setting.SettingsScreen
import com.example.fructus.ui.setting.components.OnboardingPreviewScreen
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
    // If notification tapped, bypass splash
    val startDestination = when {
        shouldOpenNotifications && targetFruitId != null -> Detail(targetFruitId, targetNotificationId)
        shouldOpenNotifications -> Notification
        else -> Splash
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ✅ Always handle onboarding via Splash, never directly
        if (!shouldOpenNotifications) {
            composable<Splash> {
                AppBackgroundScaffold {
                    SplashScreen { onboardingCompleted ->
                        if (onboardingCompleted) {
                            navController.navigate(Home) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(OnBoard) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                }
            }

            composable<OnBoard> {
                val context = LocalContext.current

                // ⬅️ Handle back button → exit app instead of going back to Splash
                BackHandler {
                    // Check if we came from settings by looking at back stack
                    val previousEntry = navController.previousBackStackEntry
                    if (previousEntry?.destination?.route?.contains("Settings") == true) {
                        // Go back to settings instead of exiting app
                        navController.navigateUp()
                    } else {
                        // Original behavior - exit app
                        (context as? Activity)?.finish()
                    }
                }

                AppBackgroundScaffold {
                    OnboardingScreen {
                        // When "Get Started" is clicked, check where we came from
                        val previousEntry = navController.previousBackStackEntry
                        if (previousEntry?.destination?.route?.contains("Settings") == true) {
                            // If came from settings, go back to settings
                            navController.navigateUp()
                        } else {
                            // Original behavior - go to Home
                            navController.navigate(Home) {
                                popUpTo<OnBoard> { inclusive = true }
                                launchSingleTop = true
                            }
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
                onSettingsClick = { navController.navigate(Settings) },
                onUserGuideClick = {navController.navigate(Guide)}
            )
        }
    }

    // Detail - ALWAYS available
    composable<Detail> { backStackEntry ->
        val args = backStackEntry.toRoute<Detail>()

        BackHandler {
            if (args.fromNotifications) {
                // ✅ Go back to Notifications screen
                navController.navigate(Notification) {
                    launchSingleTop = true
                    popUpTo(Notification) { inclusive = false }
                }
            } else if (shouldOpenNotifications) {
                // ✅ Case: app was launched from system notification
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
            notificationId = args.notificationId,
            shouldOpenNotifications = shouldOpenNotifications,
            onNavigate = {
                if (args.fromNotifications) {
                    navController.navigate(Notification) {
                        launchSingleTop = true
                        popUpTo(Notification) { inclusive = false }
                    }
                } else if (shouldOpenNotifications) {
                    navController.navigate(Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.navigateUp()
                }
            },
            onNavigateToRecipe = { name, imageResName, description ->
                navController.navigate(RecipeInfo(name, imageResName, description))
            }
        )
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

                onNotificationNavigate = { fruitId, notificationId ->
                    navController.navigate(Detail(fruitId, notificationId, fromNotifications =
                        true))
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

    // Setting
    composable<Settings> {
        AppBackgroundScaffold {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() },
                // Preview onboarding - returns to settings
                onNavigateToOnboardingPreview = {
                    navController.navigate(OnBoardPreview) {
                        launchSingleTop = true
                    }
                },
                // Fresh start - goes through full flow
                onNavigateToFreshStart = {
                    navController.navigate(Splash) {
                        popUpTo(0) { inclusive = true } // Clear entire back stack
                        launchSingleTop = true
                    }
                }
            )
        }
    }

    composable<Guide> {
        AppBackgroundScaffold {
            UserGuide(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }

    composable<OnBoardPreview> {
        LocalContext.current

        BackHandler {
            navController.navigateUp() // Always go back to settings
        }

        AppBackgroundScaffold {
            // Create a special onboarding screen for preview mode
            OnboardingPreviewScreen {
                navController.navigateUp() // Go back to settings
            }
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
            },
            onHome = {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    composable<RecipeInfo> { backStackEntry ->
        val args = backStackEntry.toRoute<RecipeInfo>()

        AppBackgroundScaffold {
            RecipeInformation(
                name = args.name,
                imageResName = args.imageResName,
                description = args.description,
                onNavigateUp = { navController.navigateUp() },
            )
        }
    }

}
