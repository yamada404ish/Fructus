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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.detail.components.SuggestedRecipe
import com.example.fructus.ui.shared.FruitAnalysis
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.loadRecipesFromJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    fruit: FruitEntity,
    onNavigate: () -> Unit
) {
//    val context = LocalContext.current
//
//    val allRecipes = context.loadRecipesFromJson()
//    val matchedRecipes = allRecipes.filter {
//        it.fruitType.equals(fruit.name.trim(), ignoreCase = true) &&
//        it.stage.equals(fruit.ripeningStage.trim(), ignoreCase = true)
//    }

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
                painter = painterResource(R.drawable.detail_lakatan),
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
                shelfLife = fruit.shelfLife,
                confidence = 90
            )

        }
    }



//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(horizontal = 24.dp)
//                .fillMaxSize()
//        ) {
//            Spacer(modifier = Modifier.height(50.dp))
//            Text(
//                text = fruit.name,
//                fontFamily = poppinsFontFamily,
//                fontWeight = FontWeight.Bold,
//                fontSize = 40.sp
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.Top
//            ) {
//                FruitStatus(
//                    ripeningProcess = fruit.ripeningProcess,
//                    shelfLife = fruit.shelfLife
//                )
//            }
//            Spacer(modifier = Modifier.height(20.dp))
//            Text(
//                text = "Current stage of ripeness:",
//                fontFamily = poppinsFontFamily,
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 20.sp
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            RipenessProgressBar(
//                currentStage = fruit.ripeningStage.toRipenessStage(),
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(3.dp))
//            Text(
//                text = "Shelf life might not be accurate due to unforeseen conditions",
//                fontFamily = poppinsFontFamily,
//                fontStyle = FontStyle.Italic,
//                fontSize = 9.9f.sp,
//                color = MaterialTheme.colorScheme.onTertiary
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Try the following:",
//                fontFamily = poppinsFontFamily,
//                fontWeight = FontWeight.Medium,
//                fontSize = 24.sp
//            )
//            Column {
//                if (matchedRecipes.isEmpty()) {
//                    Text(
//                        text = "No recipes available for this stage.",
//                        color = Color.Gray,
//                        fontSize = 14.sp
//                    )
//                } else {
//                    matchedRecipes.forEach { recipe ->
//                        SuggestedRecipe(
//                            title = recipe.name,
//                            description = recipe.description,
//                            imageRes = context.getDrawableIdByName(recipe.imageResName),
//                            modifier = Modifier.padding(vertical = 8.dp)
//                        )
//                    }
//                }
//            }
//        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDetail(
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLife: Int,
    confidence: Int,
) {
    val context = LocalContext.current
    val allRecipes = context.loadRecipesFromJson()

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
                            painter = painterResource(R.drawable.rectnagle),
                            contentDescription = "Fruit Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)

                        )
                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Text(
                                text = "$fruitName Banana",
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
                        shelfLife = shelfLife,
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

@Preview
@Composable
private fun DetailScreenContentPrev() {
    FructusTheme { 
        DetailScreenContent(
            fruit = FruitEntity(
                id = 1,
                name = "Lakatan",
                shelfLife = 300,
                ripeningStage = "ripe",
                ripeningProcess = true
            ),
            onNavigate = {}
        )
    }
}