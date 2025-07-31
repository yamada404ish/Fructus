@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fructus.ui.screens.notification

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.data.FruitNotification
import com.example.fructus.ui.screens.notification.model.Filter
import com.example.fructus.ui.shared.NotificationCard
import com.example.fructus.ui.shared.NotificationFilters
import com.example.fructus.ui.shared.displayName
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun NotificationScreen(
    notifications: List<FruitNotification>,
    onNotificationClick: (index: Int) -> Unit,
    onMarkAllAsRead: () -> Unit,
    filter: Filter,
    onSelectedFilter: (Filter) -> Unit,
    onNavigateUp: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    val scope = rememberCoroutineScope()


    Scaffold (
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(top = 6.dp, start = 16.dp, end = 16.dp),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        Modifier
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
                        text = "Notifications",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize =  22.sp,
                        letterSpacing = 0.1.sp
                    )
                },
                actions = { // <<< THIS IS WHERE YOU ADD THE SETTINGS ICON
                    IconButton(
                        onClick = onSettingsClick, // Call the new callback
                        modifier = Modifier.padding(end = 8.dp) // Add padding for spacing from edge
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.fructus_settings_icon),
                            contentDescription = "Settings",
                            modifier = Modifier.size(28.dp) // Adjust size as needed
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
//        var isChecked by remember { mutableStateOf(false)}
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NotificationFilters(items = Filter.entries.toList(), selectedFilter = filter,
                    onSelectedFilter = onSelectedFilter)

//                CustomSwitchButton(
//                    isChecked = isChecked,
//                    onCheckedChange = {isChecked = it}
//                )

            }

            Spacer(modifier = Modifier.height(20.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Today",
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA7956B)
                )
                Text (
                    "Mark all as read",
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable{
                            onMarkAllAsRead()
                        }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val filteredNotifications = when (filter) {
                Filter.All -> notifications
                Filter.Unread -> notifications.filter { !it.isRead }
            }

            filteredNotifications.forEach { notification ->
                NotificationCard(
                    fruit = notification.fruit,
                    isRead = notification.isRead,
                    onClick = {
                        val index = notifications.indexOfFirst { it.fruit.id == notification.fruit.id }
                        if (index != -1) {
                            onNotificationClick(index)
                        }
                    },

                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (filteredNotifications.isEmpty()) {
                Text(
                    "No ${filter.displayName().lowercase()} notifications",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }


//            notifications.forEachIndexed { index, notification ->
//                NotificationCard(
//                    fruit = notification.fruit,
//                    isRead = notification.isRead,
//                    onClick = {
//                        onNotificationClick(index)
//                    }
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//            }
//            NotificationListNew()
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Yesterday",
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA7956B)

            )
            Spacer(modifier = Modifier.height(14.dp))
        }


    }
}



//            NotificationListYesterday()
@Preview
@Composable
private fun NotificationScreenPrev() {
    FructusTheme {
        NotificationScreen(
            notifications = notificationList,
            onNotificationClick = {},
            onMarkAllAsRead = {},
            filter = Filter.All,
            onSelectedFilter = {}
        )
    }

}