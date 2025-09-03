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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.detail.components.SuggestedRecipe
import com.example.fructus.ui.shared.FruitAnalysis
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.getDetailBackgroundRes
import com.example.fructus.util.getDisplayFruitName
import com.example.fructus.util.getDisplayShelfLife
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.getFruitDrawableId
import com.example.fructus.util.loadRecipesFromJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    fruit: FruitEntity,
    onNavigate: () -> Unit
) {

    val shelfLifeDisplay = getDisplayShelfLife(fruit)
    val backgroundRes = getDetailBackgroundRes(fruit.name)

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 🍌 Background image
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(328.dp)
                    .align(Alignment.TopCenter)
            )

            // ← Back arrow icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 16.dp, top = 40.dp)
                    .size(32.dp)
                    .clickable(
                        onClick = { onNavigate() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            CustomBottomSheetDetail(
                fruitName = fruit.name,
                ripeningStage = fruit.ripeningStage,
                ripeningProcess = fruit.ripeningProcess,
                shelfLifeDisplay = shelfLifeDisplay,
                confidence = 90
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDetail(
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLifeDisplay: String,
    confidence: Int,
) {
    val context = LocalContext.current
    val allRecipes = context.loadRecipesFromJson()
    val displayName = getDisplayFruitName(fruitName)

    // 🔎 Filter recipes based on detected fruit + ripeness
    val matchedRecipes = allRecipes.filter {
        it.fruitType.equals(fruitName, ignoreCase = true) &&
                it.stage.equals(ripeningStage, ignoreCase = true)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.76f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF0EFE9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(getFruitDrawableId(fruitName)),
                            contentDescription = "Fruit Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(100.dp)

                        )
                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Text(
                                text = displayName,
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Text(
                                text = "It is one of the most common banana cultivars in the Philippines.",
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    FruitAnalysis(
                        ripeningStage = ripeningStage,
                        ripeningProcess = ripeningProcess,
                        shelfLifeDisplay = shelfLifeDisplay,
                        confidence = confidence,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Try the following:",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )

                    if (matchedRecipes.isEmpty()) {
                        Text(
                            text = "No recipes available for this stage.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    } else {
                        matchedRecipes.forEach { recipe ->
                            SuggestedRecipe(
                                title = recipe.name,
                                description = recipe.description,
                                imageRes = context.getDrawableIdByName(recipe.imageResName),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

