@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fructus.ui.screens.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.components.CustomSwitchButton
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    Scaffold (
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 50.dp),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        Modifier.size(30.dp)
                    )
                },
                title = {}
            )
        }
    ){ innerPadding ->
        var isChecked by remember { mutableStateOf(false)}
        Row (
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Notifications",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize =  24.sp,
                letterSpacing = 0.1.sp
            )

            CustomSwitchButton(
                isChecked = isChecked,
                onCheckedChange = {isChecked = it}
            )

        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

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