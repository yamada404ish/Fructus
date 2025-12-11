package com.example.fructus.ui.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.camera.components.Tips
import com.example.fructus.ui.camera.model.ShelfLifeRange
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily
import com.example.fructus.util.getDisplayFruitName
import com.example.fructus.util.loadRecipesFromJson
import com.example.fructus.util.responsiveDp
import com.example.fructus.util.responsiveSp
import com.example.fructus.util.toRipenessStage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    onSave:() -> Unit,
    onCancel: () -> Unit,
    onScanOtherAngle: () -> Unit,
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLifeRange: ShelfLifeRange,
    confidence: Float,
    isSaved: Boolean,
    shelfLifeDisplay: String,
    anglesScanned: Int,
    showScanOtherAngle: Boolean,

) {

    val colors = MaterialTheme.appColors



    val isSpoiled = shelfLifeRange.minDays == -1
    val disableSave = isSpoiled || isSaved

    val context = LocalContext.current
    context.loadRecipesFromJson()
    val displayName = getDisplayFruitName(fruitName)



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (isSpoiled) 0.54f else 0.68f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(colors.bg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayName,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = responsiveSp(26,28,30),
                    color = colors.textPrimary
                )
//                Spacer(modifier = Modifier.height(4.dp))

                if (anglesScanned > 1 && !isSpoiled) {
                    Surface(
                        color = colors.button,
                        shape = RoundedCornerShape(40),
                    ) {
                        Text(
                            text = if (anglesScanned == 1) "$anglesScanned angle" else "$anglesScanned angles",
                            fontSize = responsiveSp(10,12,14),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FruitAnalysis(
                        fruitName = fruitName,
                        ripeningStage = ripeningStage,
                        ripeningProcess = if (isSpoiled) false else ripeningProcess,
                        shelfLifeDisplay = shelfLifeDisplay,
                        confidence = confidence,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 👉 Only show recipes if NOT spoiled
                    if (!isSpoiled) {

                        Text(
                            text = "Tips to Prolong Shelf life:",
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize =responsiveSp(14,16,18),
                            color = colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Tips()

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (isSpoiled) {
                    // Show only one full-width button when spoiled

                    Column (
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Only show Scan Another Angle if NOT a gallery scan
                        if (showScanOtherAngle) {
                            OutlinedButton(
                                onClick = onScanOtherAngle,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                border = BorderStroke(1.dp, colors.button),
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(
                                    painter = painterResource(id = com.example.fructus.R.drawable.camera_scan_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Scan Other Angle",
                                    fontFamily = poppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                            }
                        }


                        Button(
                            onClick = onSave,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (disableSave) Color(0xFFD1D1CC) else colors.button )
                        ) {
                            Text(
                                text = when {
                                    isSpoiled -> "Fruit is already spoiled"
                                    isSaved -> "Saved" else -> "Save" },
                                color = if (isSpoiled) colors.saveText else Color.Black,
                                fontSize = responsiveSp(16,18,20),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Button(
                            onClick = onCancel,
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.button
                            )
                        ) {
                            Text(
                                "Scan Again",
                                fontFamily = poppinsFontFamily,
                                fontSize = responsiveSp(16,18,20),
                                fontWeight = FontWeight.Normal,
                                color = colors.textPrimary
                            )
                        }
                    }
                } else {
                    // Normal case: show Cancel + Save buttons
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        if (showScanOtherAngle) {
                            OutlinedButton(
                                onClick = onScanOtherAngle,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                border = BorderStroke(1.dp, colors.button),
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(
                                    painter = painterResource(id = com.example.fructus.R.drawable.camera_scan_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Scan Other Angle",
                                    fontFamily = poppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, end = 4.dp, top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(45.dp),
                                border = BorderStroke(1.dp, colors.button),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    "Cancel",
                                    fontFamily = poppinsFontFamily,
                                    fontSize = responsiveSp(16, 18, 20),
                                    fontWeight = FontWeight.Normal,
                                    color = colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = onSave,
                                enabled = !disableSave,
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(45.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (disableSave) Color(0xFFD1D1CC) else colors.button
                                )
                            ) {
                                Text(
                                    text = if (isSaved) "Saved" else "Save",
                                    color = Color.Black,
                                    fontSize = responsiveSp(16, 18, 20),
                                    fontFamily = poppinsFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun  FruitAnalysis(
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLifeDisplay: String,
    confidence: Float,
) {

    val colors = MaterialTheme.appColors

    val lowerFruitName = fruitName.lowercase()

    val bananaTypes = listOf("banana", "saba", "lakatan", "cavendish")
    val cleanName = lowerFruitName.replace("_", " ").trim()

    val ripeningProcessValue = when {
        shelfLifeDisplay == "---" -> "---"
        cleanName.contains("tomato") -> "---"
        bananaTypes.any { cleanName.contains(it) } -> "---"
        ripeningStage.equals("unripe", ignoreCase = true) -> "---"
        else -> if (ripeningProcess) "Natural" else "Artificial"
    }



    Card (
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(
            containerColor = colors.outerBox
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(responsiveDp(14,16,18)),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Fruit Analysis",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveSp(14,16,18),
                color = colors.textPrimary

            )
            Text(
                text = "Accuracy may be compromised due to unforeseen conditions",
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = responsiveSp(8,10,12),
                letterSpacing = 0.1f.sp,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                InfoCard(title = "Fruit \nShelf Life", value = shelfLifeDisplay, modifier = Modifier.weight(1f).fillMaxHeight())
                InfoCard(title = "Ripeness \nAccuracy", value = "${(confidence * 100).toInt()}%", modifier = Modifier.weight(1f).fillMaxHeight())
                InfoCard(
                    title = "Ripening \nProcess",
                    value = ripeningProcessValue,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Ripeness Stage upon scanning",
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = responsiveSp(8,10,12),
                color = colors.textSecondary,
            )
            RipenessProgressBar(
                currentStage = ripeningStage.toRipenessStage(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.appColors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.innerBox),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(responsiveDp(8,10,12)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = responsiveSp(10,12,14),
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = responsiveSp(12,14,16),
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
