package com.example.fructus.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fructus.R
import com.example.fructus.ui.components.CutoutBottomAppBar
import com.example.fructus.ui.components.FructusLogo
import com.example.fructus.ui.theme.FructusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold (
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier
                    .padding(WindowInsets.statusBars.asPaddingValues())
            ) {
                CenterAlignedTopAppBar(
                    title = { FructusLogo() }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton (
                onClick = {},
                contentColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier = Modifier.offset(y = 36.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.scan),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
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
                .padding(top = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "Your Fruits",
                fontWeight = FontWeight.ExtraBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            Image(
                painter = painterResource(R.drawable.bell),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(30.dp)
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