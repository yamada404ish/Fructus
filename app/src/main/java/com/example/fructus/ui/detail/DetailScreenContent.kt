package com.example.fructus.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.fructus.R
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.camera.components.Tips
import com.example.fructus.ui.detail.components.SuggestedRecipe
import com.example.fructus.ui.shared.FruitAnalysis
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.ClickGuard
import com.example.fructus.util.getDetailBackgroundRes
import com.example.fructus.util.getDisplayFruitName
import com.example.fructus.util.getDisplayShelfLife
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.getFruitDescription
import com.example.fructus.util.loadRecipesFromJson
import com.example.fructus.util.responsiveSp
import com.example.fructus.util.safeClickable
import kotlinx.coroutines.CoroutineScope
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    fruit: FruitEntity,
    onNavigate: () -> Unit,
    onNavigateToRecipe: (String, String, String) -> Unit
) {

    val shelfLifeDisplay = getDisplayShelfLife(fruit)
    val backgroundRes = getDetailBackgroundRes(fruit.name)

    val colors = MaterialTheme.appColors
    val clickGuard = remember { ClickGuard() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = colors.bg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }
        ) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(328.dp)
                    .align(Alignment.TopCenter)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                    .size(32.dp)
                    .safeClickable(clickGuard, coroutineScope) {
                        onNavigate()
                    }
            )

            CustomBottomSheetDetail(
                fruit = fruit,
                shelfLifeDisplay = shelfLifeDisplay,
                confidence = fruit.confidence,
                onNavigateToRecipe = onNavigateToRecipe,
                clickGuard = clickGuard,
                coroutineScope = coroutineScope
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDetail(
    fruit: FruitEntity,
    shelfLifeDisplay: String,
    confidence: Float,
    onNavigateToRecipe: (String, String, String) -> Unit,
    clickGuard: ClickGuard,
    coroutineScope: CoroutineScope,
) {

    val colors = MaterialTheme.appColors

    val context = LocalContext.current
    val allRecipes = context.loadRecipesFromJson()
    val displayName = getDisplayFruitName(fruit.name)
    val displayDescription = getFruitDescription(fruit.name)

    val matchedRecipes = allRecipes.filter {
        it.fruitType.equals(fruit.name, ignoreCase = true) &&
                it.stage.equals(fruit.ripeningStage, ignoreCase = true)
    }

    val imageModel: Any = if (!fruit.imagePath.isNullOrEmpty()) {
        File(fruit.imagePath)
    } else {
        R.drawable.unknown_fruit
    }

    val isDialogOpen = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(colors.bg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Fruit Image",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(100.dp)
                            .clickable { isDialogOpen.value = true },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.unknown_fruit),
                        error = painterResource(R.drawable.unknown_fruit)
                    )
                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = displayName,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = displayDescription,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))


                Column (
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp)
                ) {
                    FruitAnalysis(
                        fruitName = fruit.name,
                        ripeningStage = fruit.ripeningStage,
                        ripeningProcess = fruit.ripeningProcess,
                        shelfLifeDisplay = shelfLifeDisplay,
                        confidence = confidence,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tips to Prolong Shelf life:",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Tips()

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Try the following:",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )


                    if (matchedRecipes.isEmpty()) {
                        Text(
                            text = "No recipes available for this stage.",
                            color = colors.textSecondary,
                            fontSize = 14.sp
                        )
                    } else {
                        matchedRecipes.forEach { recipe ->
                            SuggestedRecipe(
                                title = recipe.name,
                                description = recipe.description,
                                imageRes = context.getDrawableIdByName(recipe.imageResName),
                                modifier = Modifier.padding(vertical = 8.dp),
                                onClick = {
                                    onNavigateToRecipe(recipe.name, recipe.imageResName, recipe.description)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
//                    Button (
//                        onClick = {},
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(45.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = colors.button
//                        )
//                    ) {
//                        Text(
//                            text = "Scan",
//                            color = Color.Black,
//                            fontSize = responsiveSp(16,18,20),
//                            fontFamily = poppinsFontFamily,
//                            fontWeight = FontWeight.Normal
//                        )
//                    }
                }
            }
            if (isDialogOpen.value) {
                Dialog(
                    onDismissRequest = { isDialogOpen.value = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable(
                                onClick = { isDialogOpen.value = false },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Full Image ${fruit.name}",
                            modifier = Modifier
                                .size(330.dp)
                                .safeClickable(clickGuard, coroutineScope) {
                                    isDialogOpen.value = false
                                }
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.unknown_fruit),
                            error = painterResource(R.drawable.unknown_fruit)
                        )
                    }
                }
            }
        }
    }
}

