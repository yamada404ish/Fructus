package com.example.fructus.ui.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.fructus.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onScanAngle: () -> Unit,
    anglesScanned: Int, // KEPT: Needed for internal logic checks
    fruitName: String,
    ripeningStage: String,
    ripeningProcess: Boolean,
    shelfLifeRange: ShelfLifeRange,
    confidence: Float,
    isSaved: Boolean,
    shelfLifeDisplay: String
) {
    val colors = MaterialTheme.appColors

    // Logic for spoiled/unsavable fruit
    val isSpoiled = shelfLifeRange.minDays == -1 || ripeningStage.contains("Spoiled", ignoreCase = true)
    val disableSave = isSpoiled || isSaved

    val context = LocalContext.current

    // Performance: Load recipes in background
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            context.loadRecipesFromJson()
        }
    }

    val displayName = getDisplayFruitName(fruitName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (isSpoiled) 0.58f else 0.75f)
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

                // Title and Angle Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayName,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = responsiveSp(28, 30, 32),
                        color = colors.textPrimary
                    )

                    // ❌ REMOVED: Angle badge display logic
                    /*
                    if (anglesScanned > 1 && !isSpoiled) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            color = colors.button,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = "$anglesScanned Angles",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    */
                }

                Spacer(modifier = Modifier.height(4.dp))

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

                    if (!isSpoiled) {
                        Text(
                            text = "Tips to Prolong Shelf life:",
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = responsiveSp(14, 16, 18),
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Tips()
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // 🔹 BUTTONS
                if (isSpoiled) {
                    // Spoiled UI
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Scan Another Angle Button (KEPT)
                        OutlinedButton(
                            onClick = onScanAngle,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            border = BorderStroke(1.dp, colors.button),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Transparent)
                        ) {
                            Text(
                                "Scan Other Angle",
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal,
                                color = colors.textPrimary
                            )
                        }

                        Button(
                            onClick = { },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1D1CC))
                        ) {
                            Text(
                                text = "Fruit is spoiled",
                                color = colors.saveText,
                                fontSize = responsiveSp(16, 18, 20),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Button(
                            onClick = onCancel,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.button)
                        ) {
                            Text(
                                "Scan New Fruit",
                                fontFamily = poppinsFontFamily,
                                fontSize = responsiveSp(16, 18, 20),
                                fontWeight = FontWeight.Normal,
                                color = colors.textPrimary
                            )
                        }
                    }
                } else {
                    // Healthy UI
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        // 🔹 Scan Angle Button (KEPT)
                        OutlinedButton(
                            onClick = onScanAngle,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            border = BorderStroke(1.dp, colors.button),
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                            // Icon retained if the resource exists
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier.weight(1f).height(50.dp),
                                border = BorderStroke(1.dp, colors.button),
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text(
                                    "Cancel",
                                    fontFamily = poppinsFontFamily,
                                    fontSize = responsiveSp(16, 18, 20),
                                    fontWeight = FontWeight.Normal,
                                    color = colors.textPrimary
                                )
                            }

                            Button(
                                onClick = onSave,
                                enabled = !disableSave,
                                modifier = Modifier.weight(1f).height(50.dp),
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

// ... (Rest of CustomBottomSheet.kt remains the same: FruitAnalysis, InfoCard) ...
@Composable
fun FruitAnalysis(
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

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.outerBox)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(responsiveDp(14, 16, 18)),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Fruit Analysis",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveSp(14, 16, 18),
                color = colors.textPrimary
            )
            Text(
                text = "Accuracy may be compromised due to unforeseen conditions",
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = responsiveSp(8, 10, 12),
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
                InfoCard(title = "Ripening \nProcess", value = ripeningProcessValue, modifier = Modifier.weight(1f).fillMaxHeight())
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Ripeness Stage upon scanning",
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = responsiveSp(8, 10, 12),
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
                .padding(responsiveDp(8, 10, 12)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = responsiveSp(10, 12, 14),
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = responsiveSp(12, 14, 16),
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}