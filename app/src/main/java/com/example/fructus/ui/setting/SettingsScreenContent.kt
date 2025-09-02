package com.example.fructus.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fructus.R
import com.example.fructus.ui.setting.components.SettingsOptionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsState,
    onNavigateUp: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
//    onEnableNotifications: () -> Unit,
//    onDismissSheet: () -> Unit,
//    onShowClearDialog: () -> Unit,
//    onClearAll: () -> Unit,
//    onDismissClearDialog: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            // Transparent top app bar with back arrow
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(16.dp)
                            .size(30.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigateUp() }
                    )
                },
                title = {}, // No title
            )
        }
    ) { innerPadding ->
        // Main content: two setting cards
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            // Toggle notification switch
            SettingsOptionCard(
                iconRes = R.drawable.bell_icon,
                title = "Allow Notifications",
                subtitle = "Receive push notifications and alerts",
                showSwitch = true,
                isChecked = state.receiveNotifications,
                onCheckedChange = onToggleNotifications
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Clear notifications card
//            SettingsOptionCard(
//                iconRes = R.drawable.fructus_trash_settings_icon,
//                iconSize = 34,
//                title = "Clear All Notifications",
//                subtitle = "Remove all existing notifications",
//                onClick = onShowClearDialog
//            )
        }
    }

    // Show bottom sheet if permission is needed
//    if (state.showSheet) {
//        EnableNotificationBottomSheet(
//            onEnableClick = onEnableNotifications,
//            onDismissClick = onDismissSheet,
//            onDismissRequest = onDismissSheet
//        )
//    }
//
//    // Show dialog to confirm clearing notifications
//    if (state.showClearDialog) {
//        ClearNotificationsDialog(
//            onDismiss = onDismissClearDialog,
//            onClearAll = onClearAll
//        )
//    }
}
