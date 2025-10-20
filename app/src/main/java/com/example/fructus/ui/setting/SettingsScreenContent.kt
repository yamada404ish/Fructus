package com.example.fructus.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fructus.R
import com.example.fructus.ui.notification.components.EnableNotificationBottomSheet
import com.example.fructus.ui.setting.components.AboutOverlay
import com.example.fructus.ui.setting.components.ClearNotificationsDialog
import com.example.fructus.ui.setting.components.SettingsOptionCard
import com.example.fructus.ui.shared.ScreenTopBar
import com.example.fructus.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsState,
    onNavigateUp: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit, // New parameter for dark mode
    onEnableNotifications: () -> Unit,
    onDismissSheet: () -> Unit,
    onShowClearDialog: () -> Unit,
    onClearAll: () -> Unit,
    onDismissClearDialog: () -> Unit,
    onShowOnboarding: () -> Unit
) {
    var showAbout by remember { mutableStateOf(false) }


    val colors = MaterialTheme.appColors

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            ScreenTopBar(
                title = "Settings",
                onNavigateUp = onNavigateUp,
                colors = colors,
                showArchive = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 24.dp ,end = 24.dp, top = 20.dp)
                .fillMaxSize()
        ) {
            SettingsOptionCard(
                iconRes = R.drawable.ic_bell,
                title = "Allow Notifications",
                showSwitch = true,
                isChecked = state.receiveNotifications,
                onCheckedChange = onToggleNotifications
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsOptionCard(
                iconRes = R.drawable.dark_mode,
                title = "Enable Dark Mode",
                showSwitch = true,
                isChecked = state.isDarkMode, // Use state from ViewModel
                onCheckedChange = onToggleDarkMode // Call the new handler
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsOptionCard(
                iconRes = R.drawable.onboard,
                title = "Onboarding",
                onClick = onShowOnboarding
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsOptionCard(
                    iconRes = R.drawable.ic_about,
                    iconSize = 34,
                    title = "About",
                    onClick = { showAbout = true },
                    modifier = Modifier.weight(1f)
                )

                SettingsOptionCard(
                    title = "Erase All Data",
                    onClick = onShowClearDialog,
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFFF55D5D),
                    contentColor = Color.White,
                    centerText = true
                )
            }
        }
    }

    // Show bottom sheet if permission is needed
    if (state.showSheet) {
        EnableNotificationBottomSheet(
            onEnableClick = onEnableNotifications,
            onDismissClick = onDismissSheet,
            onDismissRequest = onDismissSheet
        )
    }

    // Show dialog to confirm clearing notifications
    if (state.showClearDialog) {
        ClearNotificationsDialog(
            onDismiss = onDismissClearDialog,
            onClearAll = onClearAll
        )
    }

    if (showAbout) {
        AboutOverlay(onDismiss = { showAbout = false })
    }


}