// FructusApp.kt
package com.example.fructus.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.fructus.navigation.Detail
import com.example.fructus.navigation.FructusNav
import com.example.fructus.navigation.Notification
import kotlinx.coroutines.flow.MutableSharedFlow

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FructusApp(
    shouldOpenNotifications: Boolean,
    targetFruitId: Int?,
    targetNotificationId: Int?,
    notificationFlow: MutableSharedFlow<Triple<Boolean, Int?, Int?>>
) {
    val navController = rememberNavController()

    // Listen for notification taps ONLY if NOT initially from notification
    // This prevents interference with initial navigation
    if (!shouldOpenNotifications) {
        LaunchedEffect(Unit) {
            notificationFlow.collect { (openNotifications, fruitId, notificationId) ->
                if (openNotifications) {
                    if (fruitId != null) {
                        navController.navigate(Detail(fruitId, notificationId)) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Notification) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    // All navigation logic handled in FructusNav
    FructusNav(
        navController = navController,
        shouldOpenNotifications = shouldOpenNotifications,
        targetFruitId = targetFruitId,
        targetNotificationId = targetNotificationId
    )
}