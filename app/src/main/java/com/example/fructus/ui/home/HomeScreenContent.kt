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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.R
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.home.components.BottomNavBar
import com.example.fructus.ui.home.components.DraggableFruitItemWrapper
import com.example.fructus.ui.home.components.Dropdown
import com.example.fructus.ui.home.components.FructusOnboardingOverlay
import com.example.fructus.ui.home.components.FruitItem
import com.example.fructus.ui.home.model.SortOrder
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.calculateDaysSince
import com.example.fructus.util.getShelfLifeRange
import com.example.fructus.util.isFruitSpoiled
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    hasNewNotification: Boolean,
    state: HomeState,
    onFruitClick: (Int) -> Unit,
    onNotificationClick: () -> Unit,
    onScanClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onUserGuideClick: () -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(),
    isDarkMode: Boolean
) {
    var draggedFruit by remember { mutableStateOf<FruitEntity?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var showOnboarding by remember { mutableStateOf(false) }
    var trashIconPosition by remember { mutableStateOf(Offset.Zero) }

    val coroutineScope = rememberCoroutineScope()

    val colors = MaterialTheme.appColors
    val isDragging = draggedFruit != null

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = colors.bg,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier.offset(y = (60).dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                    containerColor = Color.Transparent
                ) {
                    Image(
                        painter = painterResource(
                            if (isDragging) R.drawable.trash else R.drawable.scan
                        ),
                        contentDescription = if (isDragging) "Delete Fruit" else "Scan Fruits",
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
                    .padding(top = 40.dp, bottom = 55.dp, start = 24.dp, end = 24.dp)
                    .fillMaxSize()
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Dropdown(
                        selectedItem = selectedFilter,
                        items = listOf("All", "Unripe", "Ripe", "Overripe", "Spoiling", "Spoiled"),
                        onItemSelected = { onFilterChange(it) }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.guide),
                            contentDescription = "Quick Guide",
                            modifier = Modifier
                                .size(34.dp)
                                .padding(bottom = 4.dp)
                                .clickable(
                                    onClick = { showOnboarding = true },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ),
                            tint = colors.textTertiary
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

                when {
                    state.isLoading -> {
                        LazyVerticalGrid(
                            GridCells.Fixed(2),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(4) {
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
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
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
                            "Spoiling" -> state.fruits.filter { fruit ->
                                val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
                                val estimatedShelfLife = shelfLifeRange.minDays
                                val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
                                val remainingShelfLife = estimatedShelfLife - daysSinceScan
                                remainingShelfLife == 1
                            }
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
                                    text = if (selectedFilter == "Spoiled") "No spoiled fruits available" else "No fruits available",
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
                                    .weight(1f)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                itemsIndexed(
                                    items = filteredFruits,
                                    key = { _, fruit -> fruit.id }
                                ) { _, fruit ->
                                    DraggableFruitItemWrapper(
                                        fruit = fruit,
                                        isDragging = draggedFruit?.id == fruit.id,
                                        onDragStart = { draggedFruit = it },
                                        onDrag = { dragOffset = it },
                                        onDragEnd = {
                                            // Check if fruit was dropped on trash icon
                                            draggedFruit?.let { droppedFruit ->
                                                val fruitSize = 180f
                                                val trashSize = 88f

                                                val fruitLeft = dragOffset.x
                                                val fruitTop = dragOffset.y
                                                val fruitRight = fruitLeft + fruitSize
                                                val fruitBottom = fruitTop + fruitSize

                                                val trashLeft = trashIconPosition.x
                                                val trashTop = trashIconPosition.y
                                                val trashRight = trashLeft + trashSize
                                                val trashBottom = trashTop + trashSize

                                                val isOverTrash = fruitRight >= trashLeft &&
                                                        fruitLeft <= trashRight &&
                                                        fruitBottom >= trashTop &&
                                                        fruitTop <= trashBottom

                                                if (isOverTrash) {
                                                    viewModel.deleteFruit(droppedFruit)
                                                }
                                            }

                                            coroutineScope.launch {
                                                delay(100)
                                                draggedFruit = null
                                            }

                                        }
                                    ) {
                                        if (draggedFruit?.id != fruit.id) {
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
        }



        if (isDragging) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(500f)
            )
        }


        if (isDragging) {

            val isOverTrash = remember { mutableStateOf(false) }

            // Animate scale for "hover" effect
            val scale by animateFloatAsState(
                targetValue = if (isOverTrash.value) 1.3f else 1f,
                animationSpec = tween(durationMillis = 200),
                label = "trashScale"
            )


            val shakeOffset = remember { Animatable(0f) }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(isOverTrash.value) {
                if (isOverTrash.value) {
                    coroutineScope.launch {

                        repeat(3) {
                            shakeOffset.animateTo(6f, tween(50))
                            shakeOffset.animateTo(-6f, tween(50))
                        }
                        shakeOffset.animateTo(0f, tween(50))
                    }
                } else {
                    shakeOffset.animateTo(0f, tween(100))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(998f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete Fruit",
                    modifier = Modifier
                        .size(88.dp)
                        .offset(
                            x = shakeOffset.value.dp,
                            y = (-36).dp
                        )
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                        .onGloballyPositioned { coordinates ->
                            val position = coordinates.positionInRoot()
                            trashIconPosition = position

                            draggedFruit?.let {
                                val fruitSize = 180f
                                val trashSize = 88f
                                val fruitLeft = dragOffset.x
                                val fruitTop = dragOffset.y
                                val fruitRight = fruitLeft + fruitSize
                                val fruitBottom = fruitTop + fruitSize
                                val trashLeft = position.x
                                val trashTop = position.y
                                val trashRight = trashLeft + trashSize
                                val trashBottom = trashTop + trashSize

                                isOverTrash.value = fruitRight >= trashLeft &&
                                        fruitLeft <= trashRight &&
                                        fruitBottom >= trashTop &&
                                        fruitTop <= trashBottom
                            }
                        }
                )
            }
        }


        draggedFruit?.let { fruit ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(999f)
                    .offset {
                        IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                    }
            ) {
                FruitItem(
                    fruit = fruit,
                    onFruitClick = { }
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.info),
            contentDescription = "Quick Guide",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 90.dp)
                .size(38.dp)
                .clickable(
                    onClick = { onUserGuideClick() },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            tint = Unspecified
        )

        if (showOnboarding) {
            FructusOnboardingOverlay(
                onDismiss = { showOnboarding = false }
            )
        }
    }
}