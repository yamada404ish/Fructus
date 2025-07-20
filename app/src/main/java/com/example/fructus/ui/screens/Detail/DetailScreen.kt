package com.example.fructus.ui.screens.Detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.components.DaysLeft
import com.example.fructus.ui.components.DetailCard
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(modifier: Modifier = Modifier) {
    Scaffold (
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 50.dp, start = 16.dp, end = 16.dp),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        Modifier
                            .size(32.dp)
                            .clickable(
                                onClick = {}
                            )
                    )
                },
                title = {}
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ){
            DetailCard()
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                "Lakatan",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.artificial_label),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                DaysLeft()
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Current stage of ripeness:",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Preview
@Composable
private fun DetailScreenPrev() {
    FructusTheme {
        DetailScreen()
    }

}