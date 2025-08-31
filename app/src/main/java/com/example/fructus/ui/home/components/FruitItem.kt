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
            .clip(RoundedCornerShape(20.dp))
            .clickable { onFruitClick(fruit) }
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = getDisplayFruitName(fruit.name),
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text (
                        text = fruit.scannedDate,
                        fontFamily = poppinsFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = Color(0xFF706F6F)

                    )
                }


                Box(
                    modifier = Modifier
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = displayShelfLife,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFF000000),
                        modifier = Modifier.align(Alignment.Center)
                    )
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
