package com.example.fructus.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.notification.components.EnableNotificationBottomSheet
import com.example.fructus.ui.setting.components.ClearNotificationsDialog
import com.example.fructus.ui.setting.components.SettingsOptionCard
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsState,
    onNavigateUp: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onEnableNotifications: () -> Unit,
    onDismissSheet: () -> Unit,
    onShowClearDialog: () -> Unit,
    onClearAll: () -> Unit,
    onDismissClearDialog: () -> Unit,
    onShowOnboarding: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
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
                            .size(30.dp)
                            .clickable(
                                onClick = onNavigateUp,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    )
                },
                title = {
                    Text(
                        text = "Settings",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        letterSpacing = 0.1.sp
                    )
                },
                actions = {}
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
                showSwitch = true,
                isChecked = state.receiveNotifications,
                onCheckedChange = onToggleNotifications
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Enable Dark Mode
            SettingsOptionCard(
                iconRes = R.drawable.dark_mode,
                title = "Enable Dark Mode",
                showSwitch = true,
                isChecked = false,
                onCheckedChange = {}
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Onboarding
            SettingsOptionCard(
                iconRes = R.drawable.onboard,
                title = "Onboarding",
                onClick = onShowOnboarding
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                //Clear notifications card
                SettingsOptionCard(
                    iconRes = R.drawable.about,
                    iconSize = 34,
                    title = "About",
                    onClick = {}                                                    ,
                    modifier = Modifier.weight(1f)
                )

                //Clear notifications card
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

//     Show bottom sheet if permission is needed
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
}
