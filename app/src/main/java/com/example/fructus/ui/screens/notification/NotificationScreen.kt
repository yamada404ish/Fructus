@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fructus.ui.screens.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.components.CustomSwitchButton
import com.example.fructus.ui.components.NotificationListNew
import com.example.fructus.ui.components.NotificationListYesterday
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun NotificationScreen(
    onNavigateUp: () -> Unit = {}
) {
    Scaffold (
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
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
                            .size(32.dp)
                            .clickable(
                                onClick = onNavigateUp,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    )
                },
                title = {}
            )
        },
        bottomBar = {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.fructus_logo_w_copyright),
                    contentDescription = "Fructus Logo",
                    modifier = Modifier
                        .size(120.dp)
                )
            }
        }
    ){ innerPadding ->
        var isChecked by remember { mutableStateOf(false)}
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
                Text(
                    text = "Notifications",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize =  22.sp,
                    letterSpacing = 0.1.sp
                )

                CustomSwitchButton(
                    isChecked = isChecked,
                    onCheckedChange = {isChecked = it}
                )

            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Today",
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(14.dp))
            NotificationListNew()
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Yesterday",
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(14.dp))
            NotificationListYesterday()
        }


    }
}

@Preview
@Composable
private fun NotificationScreenPrev() {
    FructusTheme {
        NotificationScreen()
    }

}