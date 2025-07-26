package com.example.fructus.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.data.FoodData
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetInformation() {
    val foods = FoodData.foods

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
                    "Lakatan",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.artificial_label),
                        contentDescription = null,
                        modifier = Modifier.height(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
//                    DaysLeft(shelfLife = shelfLife)
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
                    currentStage = RipenessStage.RIPE,
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

                LazyColumn {
                    items(foods) {food ->
                        SuggestedRecipe(
                            title = food.name,
                            description = food.description,
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
        BottomSheetInformation()
    }
}

