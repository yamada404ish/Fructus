package com.example.fructus.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.components.CutoutBottomAppBar
import com.example.fructus.ui.components.FructusLogo
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold (
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier
                    .padding(top = 50.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = { FructusLogo() }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton (
                onClick = {},
                shape = CircleShape,
                modifier = Modifier.offset(y = 40.dp),
                elevation = FloatingActionButtonDefaults.loweredElevation()
            ) {
                Image(
                    painter = painterResource(R.drawable.scan),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            CutoutBottomAppBar(modifier = Modifier.navigationBarsPadding()){}
        }
    ){ innerPadding ->

        Row (
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "Your Fruits",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize =  24.sp,
                letterSpacing = 0.1.sp
            )
            Icon (
                painter = painterResource(R.drawable.bell),
                contentDescription = "Notification",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp)
                    .clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = remember {MutableInteractionSource()}
                    )
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPrev() {
    FructusTheme {
        HomeScreen()
    }
}
