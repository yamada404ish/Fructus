package com.example.fructus.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fructus.R
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.ClickGuard
import com.example.fructus.util.calculateDaysSince
import com.example.fructus.util.getDisplayFruitName
import com.example.fructus.util.getDisplayShelfLife
import com.example.fructus.util.getShelfLifeRange
import com.example.fructus.util.safeClickable
import kotlinx.coroutines.CoroutineScope
import java.io.File

@Composable
fun FruitItem(
    modifier: Modifier = Modifier,
    fruit: FruitEntity,
    onFruitClick: (FruitEntity) -> Unit,
    clickGuard: ClickGuard,
    coroutineScope: CoroutineScope
) {

    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    val displayShelfLife = getDisplayShelfLife(fruit)
    val colors = MaterialTheme.appColors

    val backgroundColor = when {
        Regex("^spoiled", RegexOption.IGNORE_CASE).containsMatchIn(fruit.name) -> Color(0xFFBDBDBD)
        remainingShelfLife <= 1 -> Color(0xFFF3A5A5)
        remainingShelfLife == 2 -> Color(0xFFF3E5A5)
        remainingShelfLife > 2 -> Color(0xFFC2F3A5)
        else -> Color.LightGray
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
//            .height(224.dp)
            .width(148.dp)
            .safeClickable(clickGuard, coroutineScope) {
                onFruitClick(fruit)
            }
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val imageModel: Any = if (!fruit.imagePath.isNullOrEmpty()) {
                    File(fruit.imagePath)
                } else {
                    R.drawable.unknown_fruit
                }

                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(140.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = androidx.compose.ui.res.painterResource(R.drawable.unknown_fruit),
                    error = androidx.compose.ui.res.painterResource(R.drawable.unknown_fruit)
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getDisplayFruitName(fruit.name),
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fruit.scannedDate,
                            fontFamily = poppinsFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )

                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (displayShelfLife == "Spoiled!") Color(0xFFEE4949) else backgroundColor,
                                    shape = RoundedCornerShape(40)
                                )
                                .padding(horizontal = 8.dp, vertical = 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayShelfLife,
                                fontFamily = poppinsFontFamily,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (displayShelfLife == "Spoiled!") Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}

//@Preview
//@Composable
//private fun FruitItemPrev() {
//    FructusTheme {
//        FruitItem(
//            modifier = Modifier,
//            fruit = FruitEntity(
//                id = 1,
//                name = "Tomato",
//                shelfLife = 3,
//                ripeningStage = "ripe",
//                ripeningProcess = true,
//
//            ),
//            onFruitClick = {}
//        )
//    }
//}