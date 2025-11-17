package com.example.fructus.ui.detail.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.shared.ScreenTopBar
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.ClickGuard
import com.example.fructus.util.getDrawableIdByName
import com.example.fructus.util.loadRecipesFromJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeInformation(
    onNavigateUp: () -> Unit,
    name: String,
    imageResName: String,
    description: String,
) {

    val colors = MaterialTheme.appColors
    val context = LocalContext.current
    val imageRes = context.getDrawableIdByName(imageResName)

    val recipe = remember {
        context.loadRecipesFromJson().find { it.name.equals(name, ignoreCase = true) }
    }

    var selectedTab by remember { mutableStateOf("Ingredients") }
    val clickGuard = remember { ClickGuard() }
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        containerColor = colors.bg,
        topBar = {
            ScreenTopBar(
                title = "Recipe Information",
                onNavigateUp = onNavigateUp,
                colors = colors,
                showArchive = false,
                clickGuard = clickGuard,
                coroutineScope = coroutineScope
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 24.dp ,end = 24.dp, top = 20.dp)
                .fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Crop, // keeps proportions & fills rounded area
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp)) // 👈 adds corner radius
                )

                Spacer(modifier = Modifier.width(20.dp))
                Column (
                    verticalArrangement = Arrangement.Center
                ) {
                    Text (
                        text = name,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = colors.textPrimary
                    )
                    Text (
                        text = "Description",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.textSecondary
                    )
                    Text (
                        text = description,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = colors.textPrimary
                    )

                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row (
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ingredients",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (selectedTab == "Ingredients") colors.textPrimary else colors.textSecondary,
                        modifier = Modifier
                            .clickable (
                                onClick = { selectedTab = "Ingredients"},
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .background(
                                if (selectedTab == "Ingredients") colors.innerBox else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer (modifier = Modifier.width(20.dp))

                Text(
                    text = "Preparations",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (selectedTab == "Preparations") colors.textPrimary else colors.textSecondary,
                    modifier = Modifier
                        .clickable (
                            onClick = { selectedTab = "Preparations"},
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .background(
                            if (selectedTab == "Preparations") colors.innerBox else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (selectedTab == "Ingredients") {
                    Column (
                        modifier = Modifier
                            .padding(top = 16.dp, start = 6.dp, end = 6.dp)
                    ){
                        recipe?.ingredients?.forEach { ingredient ->
                            Text(
                                text = "● $ingredient",
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = colors.textPrimary
                            )
                            HorizontalLine(
                                color = Color(0xFFD1CEBA)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                } else {
                    Column (
                        modifier = Modifier
                            .padding(top = 16.dp, start = 6.dp, end = 6.dp)
                    ){
                        recipe?.directions?.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(colors.ingr, shape = RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontFamily = poppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF000000)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = step,
                                    fontFamily = poppinsFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
