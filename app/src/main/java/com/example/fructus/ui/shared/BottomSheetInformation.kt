package com.example.fructus.ui.shared


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.camera.model.ShelfLifeRange
import com.example.fructus.ui.detail.components.SuggestedRecipe
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.getDisplayFruitName
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.loadRecipesFromJson
import com.example.fructus.util.toRipenessStage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    onSave:() -> Unit,
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLifeRange: ShelfLifeRange,
    confidence: Int,
    isSaved: Boolean,
    shelfLifeDisplay: String



) {
    val isSpoiled = shelfLifeRange.minDays == -1
    val disableSave = isSpoiled || isSaved

    val context = LocalContext.current
    val allRecipes = context.loadRecipesFromJson()
    val displayName = getDisplayFruitName(fruitName)


    // 🔎 Filter recipes based on detected fruit + ripeness
    val matchedRecipes = allRecipes.filter {
        it.fruitType.equals(fruitName, ignoreCase = true) &&
                it.stage.equals(ripeningStage, ignoreCase = true)
    }

//    var isSaved by remember {mutableStateOf(false)}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (isSpoiled) 0.55f else 0.84f)
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

                Text(
                    text = displayName,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                Text(
                    text = "It is one of the most common banana cultivars in the Philippines.",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                FruitAnalysis(
                    ripeningStage = ripeningStage,
                    ripeningProcess = if (isSpoiled) false else ripeningProcess,
                    shelfLifeDisplay = shelfLifeDisplay, // Use -1 to represent
                    // "---"
                    confidence = confidence,
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 👉 Only show recipes if NOT spoiled
                if (!isSpoiled) {
                    Text(
                        text = "Try the following:",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )

                    // Scrollable content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (matchedRecipes.isEmpty()) {
                            // shrink content
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No recipes available for this stage.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
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

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fixed Save button at the bottom
                Button(
                    onClick = onSave,
                    enabled = !disableSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (disableSave) Color(0xFFD1D1CC) else Color(0xFFBADBA2)
                    )
                ) {
                    Text(
                        text = when {
                            isSpoiled -> "Fruit is already spoiled"
                            isSaved -> "Saved"
                            else -> "Save"
                        },
                        color = if (isSpoiled || isSaved) Color(0xFF726F6F) else Color.Black,
                        fontSize = 18.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }

            }
        }
    }
}

@Composable
fun FruitAnalysis(
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLifeDisplay: String,
    confidence: Int,
) {

    Card (
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8E6D5)
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Fruit Analysis",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Accuracy may be compromised due to unforeseen conditions",
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 10.sp,
                letterSpacing = 0.1f.sp,
                color = Color(0xFF6B6767)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                InfoCard(title = "Shelf Life", value = shelfLifeDisplay)
                InfoCard(title = "Confidence", value = "$confidence%")
                InfoCard(title = "Process", value = if (shelfLifeDisplay == "---") "---" else if
                        (ripeningProcess) "Natural" else "Artificial")
            }
            Spacer(modifier = Modifier.height(16.dp))
            RipenessProgressBar(
                currentStage = ripeningStage.toRipenessStage(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(90.dp)
            .width(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1CEBA)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the card space
                .padding(10.dp),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


//
//@Preview
//@Composable
//private fun CustomBottomSheetPrev() {
//    FructusTheme {
//        CustomBottomSheet(
//            fruitName = "Cavendish",
//            ripeningStage = "Ripe",
//            ripeningProcess = false,
//            shelfLife = 3,
//            confidence = 90,
//            isSaved = true,
//            onSave = {}
//
//        )
//    }
//}