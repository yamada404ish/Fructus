package com.example.fructus.ui.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.data.local.FruitDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    fruitId: Int,
    notificationId: Int? = null, // 👈 add optional param
    shouldOpenNotifications: Boolean = false,
    onNavigate: () -> Unit,
    onNavigateToRecipe: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { FruitDatabase.getDatabase(context) }

    // get fruit
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(db.fruitDao(), fruitId)
    )

    // ✅ If this detail came from a notification, mark it as read once
    LaunchedEffect(notificationId, shouldOpenNotifications) {
        if (shouldOpenNotifications) { // Coming from push notification
            android.util.Log.d("DetailScreen", "Marking notifications as read for fruitId: $fruitId")

            val notificationDao = db.notificationDao()
            // Get all unread notifications for this fruit
            val unreadNotifications = notificationDao.getNotificationsByFruitId(fruitId)
                .filter { !it.isRead }

            // Mark them all as read
            unreadNotifications.forEach { notification ->
                notificationDao.updateNotification(notification.copy(isRead = true))
                android.util.Log.d("DetailScreen", "Marked notification ${notification.id} as read: ${notification.message}")
            }
        } else if (notificationId != null) {
            // Coming from in-app notification click
            val notificationDao = db.notificationDao()
            val notification = notificationDao.getNotificationById(notificationId)
            notification?.let {
                notificationDao.updateNotification(it.copy(isRead = true))
            }
        }
    }

    val fruit = viewModel.fruit.collectAsState().value
    fruit?.let {
        DetailScreenContent(
            fruit = it,
            onNavigate = onNavigate,
            onNavigateToRecipe = onNavigateToRecipe
        )
    }
}



/*
This screen is shown when a fruit is selected from the Home screen.

It uses the fruitId to fetch the fruit from the Room database.

Shows a loading spinner while waiting.

When the fruit is loaded, it passes it to DetailScreenContent to show the details.
*/