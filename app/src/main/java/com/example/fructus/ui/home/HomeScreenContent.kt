package com.example.fructus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.home.components.FructusLogo
import com.example.fructus.ui.home.components.FruitItem
import com.example.fructus.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onFruitClick: (Int) -> Unit,
    onNotificationClick: () -> Unit,
) {

    Scaffold (
        containerColor = Color.Transparent,

        // Top app bar with logo
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
        // Large floating scan button
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
                            onClick = { },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {}
    ){ innerPadding ->

        // Main content area
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // Top Row with title and notification icon
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
                // Notification bell icon
                Icon (
                    painter = painterResource(R.drawable.bell),
                    contentDescription = "Notification",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            onClick = { onNotificationClick() },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
            Spacer(Modifier.size(16.dp))

            if (state.fruits.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image (
                        painter = painterResource(R.drawable.fructus_empty_icon),
                        contentDescription = "No fruits available",
                        modifier = Modifier
                            .size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No fruits available",
                        color = Color(0xFF9D9076),
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

            } else {
                // Grid of fruit items (2 per row)
                LazyVerticalGrid(
                    GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 130.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ){
                    itemsIndexed(state.fruits) { index, fruit ->
                        FruitItem(
                            fruit = fruit,
                            onFruitClick = { onFruitClick(fruit.id) }
                        )
                    }
                }
            }
        }
    }
}
