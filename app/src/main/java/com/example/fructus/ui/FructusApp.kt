// 6. Updated FructusApp.kt - Apply dark mode theme
package com.example.fructus.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.fructus.navigation.Detail
import com.example.fructus.navigation.FructusNav
import com.example.fructus.navigation.Notification
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.util.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FructusApp(
    shouldOpenNotifications: Boolean,
    targetFruitId: Int?,
    targetNotificationId: Int?,
    notificationFlow: MutableSharedFlow<Triple<Boolean, Int?, Int?>>
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val navController = rememberNavController()

    // Observe dark mode preference
    val isDarkMode by dataStoreManager.darkModeFlow.collectAsState(initial = false)

    // Listen for notification taps ONLY if NOT initially from notification
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

    // Apply the theme with dark mode state
    FructusTheme(darkTheme = isDarkMode) {
        // All navigation logic handled in FructusNav
        FructusNav(
            navController = navController,
            shouldOpenNotifications = shouldOpenNotifications,
            targetFruitId = targetFruitId,
            targetNotificationId = targetNotificationId
        )
    }
}