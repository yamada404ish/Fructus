package com.example.fructus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fructus.R
import com.example.fructus.data.DummyFruitDataSource.fruitList
import com.example.fructus.navigation.Notification
import com.example.fructus.ui.home.components.FructusLogo
import com.example.fructus.ui.home.components.FruitItem
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onFruitClick: ( Int) -> Unit
) {
    Scaffold (
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .padding(top = 50.dp),
            ) {
                CenterAlignedTopAppBar(
                    title = { FructusLogo() },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )

            }
        },
        floatingActionButton = {
            FloatingActionButton (
                onClick = {},
                modifier = Modifier.offset(y = (-10).dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                containerColor = Color.Transparent
            ) {
                Image(
                    painter = painterResource(R.drawable.scan),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clickable(
                            onClick = {},
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {}
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
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
                        .size(30.dp)
                        .clickable(
                            onClick = {
                                navController.navigate(Notification)
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
            Spacer(Modifier.size(16.dp))
            LazyVerticalGrid(
                GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 130.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ){
                itemsIndexed(fruitList) { index, fruit ->
                    FruitItem (
                        fruit = fruit,
                    ) {
                        onFruitClick(index)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPrev() {
    FructusTheme {
//        HomeScreen()
    }
}
