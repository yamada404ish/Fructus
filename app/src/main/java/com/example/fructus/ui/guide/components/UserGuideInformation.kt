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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.fructus.ui.guide.model.FruitGuide
import com.example.fructus.ui.guide.model.FruitStage
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.loadFruitGuides
import com.example.fructus.util.responsiveSp

@Composable
fun UserGuideInformation(
    modifier: Modifier = Modifier,
    selectedFruit: String,
    selectedProcess: String,
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
            FruitGuideCard(guide = guide, bgColor = colors.card, selectedProcess = selectedProcess)
        }
    }
}

@Composable
fun FruitGuideCard(
    guide: FruitGuide,
    bgColor: Color,
    selectedProcess: String
) {
    val colors = MaterialTheme.appColors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = guide.fruitName,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = responsiveSp(26,28,30),
            textAlign = TextAlign.Center,
            color = colors.textPrimary
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
                        fontSize = responsiveSp(18,20,22),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        color = colors.textPrimary
                    )

                    Text(
                        "Stage",
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        fontSize = responsiveSp(18,20,22),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        color = colors.textPrimary
                    )
                    Text(
                        "Shelf life",
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFontFamily,
                        fontSize = responsiveSp(18,20,22),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        color = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                guide.stages.forEach { stage ->
                    FruitStageRow(stage = stage, selectedProcess = selectedProcess)
                }
            }
        }
        Text(
            text = if (selectedProcess == "Natural") {
                "Environmental factors may affect shelf life."
            } else {
                "Shelf life may vary between different artificial ripening agents."
            },
            fontFamily = poppinsFontFamily,
            fontSize = 10.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            color = colors.textSecondary,
            softWrap = true,
            modifier = Modifier.width(250.dp)
        )
    }
}

@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
fun FruitStageRow(
    stage: FruitStage,
    selectedProcess: String
) {

    val colors = MaterialTheme.appColors

    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        stage.image, "drawable", context.packageName
    )

    val isDialogOpen = remember { mutableStateOf(false)}
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
                .size(60.dp)
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
            fontSize = responsiveSp(14,16,18),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            color = colors.textPrimary
        )

        Text(
            text = if (selectedProcess == "Natural") {
                stage.shelfLifeNatural
            } else {
                stage.shelfLifeArtificial
            },
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = responsiveSp(13,15,17),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            color = when (stage.stage) {
                "Unripe" -> {
                    colors.unripe
                }
                "Ripe" -> {
                    colors.ripe
                }
                "Overripe" -> {
                    colors.overripe
                }
                else -> {
                    colors.spoiled
                }
            }
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
                    .background(Color.Black.copy(alpha = 0.6f))
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


