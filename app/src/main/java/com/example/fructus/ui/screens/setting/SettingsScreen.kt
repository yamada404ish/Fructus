package com.example.fructus.ui.screens.setting


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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.fructus.R
import com.example.fructus.ui.shared.EnableNotificationBottomSheet
import com.example.fructus.ui.shared.SettingsOptionCard
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.util.isNotificationPermissionGranted
import com.example.fructus.util.navigateToNotificationSettings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showSheet by remember { mutableStateOf(false)}
    var isChecked by remember { mutableStateOf(false) }
    var fromSettings by remember { mutableStateOf(false) }


    LaunchedEffect(fromSettings) {
        if (fromSettings) {
            isChecked = isNotificationPermissionGranted(context)
            fromSettings = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 6.dp, start = 16.dp, end = 16.dp),
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
                title = {}
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            SettingsOptionCard(
                iconRes = R.drawable.bell_icon,
                title = "Allow Notifications",
                subtitle = "Receive push notifications and alerts",
                showSwitch = true,
                isChecked = isChecked,
                onCheckedChange = {
                    if (!isChecked) {
                    showSheet = true // show bottom sheet before confirming
                    } else {
                        isChecked = false // allow turning OFF instantly
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsOptionCard(
                iconRes = R.drawable.fructus_trash_settings_icon,
                iconSize = 34,
                title = "Clear All Notifications",
                subtitle = "Remove all existing notifications",
                onClick = {
                    // TODO: Add delete logic here
                }
            )
        }
    }
    if (showSheet) {
        EnableNotificationBottomSheet(
            onEnableClick = {
                // Confirm enabling logic
                navigateToNotificationSettings(context = context)
                fromSettings = true
                showSheet = false
            },
            onDismissClick = {
                showSheet = false
                isChecked = false
            },
            onDismissRequest = {
                isChecked = false
                showSheet = false
            }
        )
    }
}



@Preview
@Composable
private fun SettingScreenPrev() {
    FructusTheme {
        SettingsScreen()
    }
}