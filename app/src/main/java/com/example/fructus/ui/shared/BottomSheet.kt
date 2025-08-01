package com.example.fructus.ui.shared

//import com.example.fructus.data.FoodData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.data.Fruit
import com.example.fructus.ui.detail.FruitStatus
import com.example.fructus.ui.detail.components.SuggestedRecipe
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.loadRecipesFromJson
import com.example.fructus.util.toRipenessStage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetInformation(fruit: Fruit) {
    val context = LocalContext.current
    val allRecipes = context.loadRecipesFromJson()

    val matchedRecipes = allRecipes.filter {
        it.fruitType.equals(fruit.name, ignoreCase = true) &&
                it.stage.equals(fruit.ripeningStage, ignoreCase = true)
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 300.dp,
        sheetContent = {
            Column (
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            ) {
                Text(
                    fruit.name,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    FruitStatus(
                        ripeningProcess = fruit.ripeningProcess,
                        shelfLife = fruit.shelfLife
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
                    currentStage = fruit.ripeningStage.toRipenessStage(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Shelf life might not be accurate due to unforeseen conditions",
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
    ){ innerPadding ->

    }
}



@Preview
@Composable
private fun BottomSheetInformationPrev() {
    FructusTheme {
        BottomSheetInformation(
            fruit = Fruit(
                id = 1,
                name = "Cavendish",
                shelfLife = 5,
                ripeningStage = "overripe",
                ripeningProcess = false,
                image = R.drawable.lakatan
            )
        )
    }
}

