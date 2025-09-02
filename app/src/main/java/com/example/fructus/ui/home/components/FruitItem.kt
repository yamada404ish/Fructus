package com.example.fructus.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.calculateDaysSince
import com.example.fructus.util.getDisplayFruitName
import com.example.fructus.util.getDisplayShelfLife
import com.example.fructus.util.getFruitDrawableId
import com.example.fructus.util.getShelfLifeRange

@Composable
fun FruitItem(
    modifier: Modifier = Modifier,
    fruit: FruitEntity,
    onFruitClick: (FruitEntity) -> Unit
) {

    val shelfLifeRange = getShelfLifeRange(fruit.name, fruit.ripeningStage)
    val estimatedShelfLife = shelfLifeRange.minDays
    val daysSinceScan = calculateDaysSince(fruit.scannedTimestamp)
    val remainingShelfLife = estimatedShelfLife - daysSinceScan

    val displayShelfLife = getDisplayShelfLife(fruit)

    val backgroundColor = when {
        Regex("^spoiled", RegexOption.IGNORE_CASE).containsMatchIn(fruit.name) -> Color(0xFFBDBDBD)
        remainingShelfLife <= 1 -> Color(0xFFF3A5A5)
        remainingShelfLife == 2 -> Color(0xFFF3E5A5)
        remainingShelfLife > 2 -> Color(0xFFC2F3A5)
        else -> Color.LightGray
    }


    Card (
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onFruitClick(fruit) }
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(getFruitDrawableId(fruit.name)),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // 🍌 Fruit name (centered above)
                    Text(
                        text = getDisplayFruitName(fruit.name),
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    // 📅 Scanned date and 🕒 Shelf life side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fruit.scannedDate,
                            fontFamily = poppinsFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = Color(0xFF706F6F)
                        )

                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (displayShelfLife) {
                                        "Spoiled!" -> Color(0xFFEE4949) // 🔴 Red for spoiled
                                        else -> backgroundColor
                                    },
                                    shape = RoundedCornerShape(40)
                                )
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayShelfLife,
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = when (displayShelfLife) {
                                    "Spoiled!" -> Color(0xFFFFFFFF) // ⚪ White text for spoiled
                                    else -> Color.Black
                                }
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