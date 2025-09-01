package com.example.fructus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.R
import com.example.fructus.ui.home.components.BottomNavBar
import com.example.fructus.ui.home.components.FructusLogo
import com.example.fructus.ui.home.components.FruitFilterToggle
import com.example.fructus.ui.home.components.FruitItem
import com.example.fructus.ui.home.model.SortOrder
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.isFruitSpoiled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onFruitClick: (Int) -> Unit,
    onNotificationClick: () -> Unit,
    onScanClick: () -> Unit,
    onSettingsClick: () -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()


) {

    Scaffold(
        containerColor = Color.Transparent,

        topBar = {
            Column(
                modifier = Modifier
                    .padding(top = 10.dp),
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
            FloatingActionButton(
                onClick = { onScanClick() }, // ✅ enabled scan function
                modifier = Modifier.offset(y = (60).dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                containerColor = Color.Transparent
            ) {
                Image(
                    painter = painterResource(R.drawable.scan),
                    contentDescription = "Scan Fruits",
                    modifier = Modifier
                        .size(88.dp)
                        .clickable(
                            onClick = { onScanClick() },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomNavBar(
                onNotificationClick = onNotificationClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "Your Fruits",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 0.1.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FruitFilterToggle(
                    selected = selectedFilter,
                    onSelect = { onFilterChange(it) }
                )

                Icon(
                    painter = painterResource(
                        if (state.sortOrder == SortOrder.OLDEST) R.drawable.sort_newest else R.drawable.sort_oldest
                    ),
                    contentDescription = "Sort Fruits",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            onClick = { viewModel.toggleSortOrder() },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )

            }
            Spacer(Modifier.size(16.dp))

            when {
                state.isLoading -> {
                    LazyVerticalGrid(
                        GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        items(4) { // Show 6 skeleton items
                            Box(
                                modifier = Modifier
                                    .height(180.dp)
                                    .background(
                                        Color.Gray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            )
                        }
                    }
                }
                state.fruits.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.empty),
                            contentDescription = "No fruits available",
                            modifier = Modifier.size(200.dp)
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
                }
                else -> {

                    val filteredFruits = when (selectedFilter) {
                        "Spoiled" -> state.fruits.filter { isFruitSpoiled(it) }
                        else -> state.fruits
                    }

                    if (filteredFruits.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.empty),
                                contentDescription = "No fruits available",
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (selectedFilter == "Spoiled") "No spoiled fruits " +
                                        "available" else "No fruits available",
                                color = Color(0xFF9D9076),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(bottom = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            itemsIndexed(filteredFruits) { _, fruit ->
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
    }
}

@Preview
@Composable
private fun HomeScreenPrev() {
    FructusTheme {
        HomeScreenContent(
            state = HomeState(),
            onFruitClick = {},
            onNotificationClick = {},
            onScanClick = {},
            onSettingsClick = {},
            selectedFilter = "All",
            onFilterChange = {}
        )

    }
}