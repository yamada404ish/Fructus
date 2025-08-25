package com.example.fructus.ui.camera.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.detail.components.FruitStatus
import com.example.fructus.ui.shared.RipenessProgressBar
import com.example.fructus.ui.detail.components.SuggestedRecipe
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.loadRecipesFromJson
import com.example.fructus.util.toRipenessStage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetInformation(
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLife: Int,
) {
    val context = LocalContext.current
    val allRecipes = context.loadRecipesFromJson()

    val matchedRecipes = allRecipes.filter {
        it.fruitType.equals(fruitName, ignoreCase = true) &&
                it.stage.equals(ripeningStage, ignoreCase = true)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 280.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            ) {
                Text(
                    fruitName,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FruitStatus(
                        ripeningProcess = ripeningProcess,
                        shelfLife = shelfLife
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Current stage of ripeness:",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                RipenessProgressBar(
                    currentStage = ripeningStage.toRipenessStage(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (ripeningStage.equals("Spoiled", true)) {
                        "Shelf life: Not edible"
                    } else {
                        "Shelf life might not be accurate due to unforeseen conditions"
                    },
                    fontFamily = poppinsFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Try the following:",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp
                )

                Column {
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
    ) { }
}
