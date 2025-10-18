package com.example.fructus.ui.guide.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.fructus.ui.shared.model.Guide
import com.example.fructus.ui.guide.model.FruitGuide
import com.example.fructus.ui.guide.model.FruitStage
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.loadFruitGuides

@Composable
fun UserGuideInformation(
    modifier: Modifier = Modifier,
    selectedFruit: String,
    selectedGuide: Guide
) {
    val context = LocalContext.current
    val fruitGuides = loadFruitGuides(context)
    val colors = MaterialTheme.appColors

    val filteredGuides = fruitGuides.filter {
        it.type.equals(selectedFruit.lowercase(), ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        filteredGuides.forEach { guide ->
            FruitGuideCard(guide = guide, bgColor = colors.card, selectedGuide = selectedGuide)
        }
    }
}

@Composable
fun FruitGuideCard(
    guide: FruitGuide, selectedGuide: Guide, bgColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = guide.fruitName,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Color",
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Stage",
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Shelf life",
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                guide.stages.forEach { stage ->
                    FruitStageRow(stage = stage, selectedGuide = selectedGuide)
                }



            }
        }
    }
}

@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
fun FruitStageRow(stage: FruitStage, selectedGuide: Guide) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        stage.image, "drawable", context.packageName
    )

    var isDialogOpen = remember { mutableStateOf(false)}
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(imageResId),
            contentDescription = stage.stage,
            modifier = Modifier
                .weight(1f)
                .size(55.dp)
                .align(Alignment.CenterVertically)
                .clickable(
                    onClick = { isDialogOpen.value = true },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            tint = Unspecified
        )

        Text(
            text = stage.stage,
            fontFamily = poppinsFontFamily,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )

        Text(
            text = when (selectedGuide) {
                Guide.Natural -> stage.shelfLifeNatural
                Guide.Artificial -> stage.shelfLifeArtificial
                else -> {stage.shelfLifeNatural}
            },
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center

        )

    }

    if (isDialogOpen.value) {
        Dialog(
            onDismissRequest = { isDialogOpen.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)) // 👈 dim background
                    .clickable(
                        onClick = { isDialogOpen.value = false },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(imageResId),
                    contentDescription = "Full Image ${stage.stage}",
                    modifier = Modifier
                        .size(330.dp)
                        .clickable(
                            onClick = { isDialogOpen.value = false },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Unspecified
                )
            }
        }
    }


}


