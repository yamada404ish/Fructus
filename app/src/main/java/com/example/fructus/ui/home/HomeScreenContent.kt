package com.example.fructus.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.R
import com.example.fructus.ui.home.components.BottomNavBar
<<<<<<< HEAD
import com.example.fructus.ui.home.components.FructusGuideStep
=======
import com.example.fructus.ui.home.components.Dropdown
>>>>>>> last
import com.example.fructus.ui.home.components.FructusLogo
import com.example.fructus.ui.home.components.FruitItem
import com.example.fructus.ui.home.model.SortOrder
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.isFruitSpoiled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    hasNewNotification: Boolean,
    state: HomeState,
    onFruitClick: (Int) -> Unit,
    onNotificationClick: () -> Unit,
    onScanClick: () -> Unit,
    onSettingsClick: () -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(),
    isDarkMode: Boolean

) {

    var showOnboarding by remember { mutableStateOf(false) }

    val colors = MaterialTheme.appColors



        Scaffold(
            containerColor = colors.bg,

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
                    hasNewNotification = hasNewNotification,
                    onNotificationClick = onNotificationClick,
                    onSettingsClick = onSettingsClick,
                    isDarkMode = isDarkMode
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
                    color = colors.textPrimary,
                    letterSpacing = 0.1.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Dropdown(
                        selectedMenu = selectedFilter,
                        onMenuSelected = { onFilterChange(it) }
                    )

//                    FruitFilterToggle(
//                        selected = selectedFilter,
//                        onSelect = { onFilterChange(it) }
//                    )

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.guide), // Use your help icon or create one
                            contentDescription = "Quick Guide",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(
                                    onClick = { showOnboarding = true },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ),
                            tint = Color(0xFF718860) // Adjust color to match your theme
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
                            tint = colors.textTertiary
                        )
                    }

                }
                Spacer(Modifier.height(16.dp))

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
                            Icon(
                                painter = painterResource(R.drawable.empty),
                                contentDescription = "No fruits available",
                                modifier = Modifier.size(200.dp),
                                tint = colors.textTertiary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No fruits available",
                                color = colors.textSecondary,
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }

                    else -> {

                        val filteredFruits = when (selectedFilter) {
                            "All" -> state.fruits
                            "Unripe" -> state.fruits.filter { it.ripeningStage.equals("unripe", true) }
                            "Ripe" -> state.fruits.filter { it.ripeningStage.equals("ripe", true) }
                            "Overripe" -> state.fruits.filter { it.ripeningStage.equals("overripe", true) }
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
                                Icon(
                                    painter = painterResource(R.drawable.empty),
                                    contentDescription = "No fruits available",
                                    modifier = Modifier.size(200.dp),
                                    tint = colors.textTertiary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (selectedFilter == "Spoiled") "No spoiled fruits " +
                                            "available" else "No fruits available",
                                    color = colors.textSecondary,
                                    fontFamily = poppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 26.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                itemsIndexed(filteredFruits) { _, fruit ->
                                    FruitItem(
                                        fruit = fruit,
                                        onFruitClick = { onFruitClick(fruit.id) }
                                    )
                                }
                                item(span = { GridItemSpan(2) }) { // make spacer span full row
                                    Spacer(modifier = Modifier.height(0.2f.dp))
                                }

                            }
                        }
                    }
                }
            }
        }
        if (showOnboarding) {
            FructusOnboardingOverlay(
                onDismiss = { showOnboarding = false }
            )
        }
}

@Composable
fun FructusOnboardingOverlay(
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

        // Animation states
        val animatedAlpha by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(400)
        )

        val animatedScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(400, easing = EaseOutBounce)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier

                .fillMaxSize()
                .zIndex(1000f)
                .alpha(animatedAlpha)
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() }

        ) {


            AnimatedVisibility(
                visible = isVisible, // Adjust bottom padding based on your carousel height
                enter = fadeIn(animationSpec = tween(300, delayMillis = 100)
                )
            ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .scale(animatedScale)
                    .align(Alignment.Center)
                    .clickable { },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with fruit image
                    Image(
                        painter = painterResource(id = R.drawable.fructus_logo),
                        contentDescription = "",
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Fit
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "How to use",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        color = Color(0xFF718860), // Green theme
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Scan and know about your fruits!",
                        fontSize = 16.sp,
                        fontFamily = poppinsFontFamily,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Guide steps
                    FructusGuideStep(
                        painterResource(R.drawable.scan),
                        title = "Scan Your Fruits",
                        description = "Tap the scan button below to add new fruits"
                    )

                    FructusGuideStep(
                        iconRes = painterResource(R.drawable.sort_oldest),
                        title = "Filter & Sort",
                        description = "Use the filter on the top right to toggle to or sort by newest/oldest"
                    )

                    FructusGuideStep(
                        painterResource(R.drawable.ic_bell),
                        title = "Access Notifications",
                        description = "Tap the notification icon to see your notifications"
                    )

                    FructusGuideStep(
                        painterResource(R.drawable.settings),
                        title = "Settings",
                        description = "Access settings to personalize your fruit tracking experience"
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBADBA2)
                            )
                        ) {
                            Text(
                                "Got It!",
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }
                }
            }
        }

}
