
//on HowTo() composable how to make when i click the l drawable the text
//
//" Keep the fruit inside " will change to " Check the lighting " text and if i click again the "Check the lighting text " will change to " Press Scan "
//
//
//then the lottie raw from how_to2 to how_to1 and then it will go to how_to3
//
//
//
//then the icon drawable from one will change to two then to three
//
//
//and the text "Make sure the entire fruit is captured within the box." will change to "Make sure fruit is well lit, if not use the flashlight" and then will from that will change to "Press scan to capture fruit"
//
//
//
//
//so the l drawable can be click 3 times





package com.example.fructus.ui.camera.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fructus.R
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun HowTo(
    modifier: Modifier,
    onClose: () -> Unit
) {

    val colors = MaterialTheme.appColors

    var stepIndex by remember { mutableStateOf(0) }
    val step = howToSteps[stepIndex]

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.how_to2))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card (
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
                .height(536.dp)
                .clickable(
                    onClick = { },
                    indication = null, // 🔥 disables ripple
                    interactionSource = remember { MutableInteractionSource() }
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.bg
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column (
                modifier = Modifier
                    .padding(top = 28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    step.title,
                    fontFamily = poppinsFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 44.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(step.tutorialImg),
                    contentDescription = "Tutorial",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(200.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row (
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(R.drawable.l), // Use your help icon or create one
                        contentDescription = "Previous",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable(
                                enabled = stepIndex > 0,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (stepIndex > 0) stepIndex--
                            },
                        tint = if (stepIndex > 0) colors.textSecondary else Color.Gray
                    )
                    Icon(
                        painter = painterResource(step.iconRes), // Use your help icon or create one
                        contentDescription = "Step Icon",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable(
                                onClick = {  },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        tint = Color.Unspecified
                    )
                    Icon(
                        painter = painterResource(R.drawable.r), // Use your help icon or create one
                        contentDescription = "Next",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable(
                                enabled = stepIndex < howToSteps.lastIndex,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (stepIndex < howToSteps.lastIndex) stepIndex++
                            },
                        tint = if (stepIndex < howToSteps.lastIndex) colors.textSecondary else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(0.dp))

                Text(
                    step.subtitle,
                    fontFamily = poppinsFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .padding(24.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onClose ()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .height(40.dp)
                        .padding(horizontal = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.button, // background
                    ),
                    shape = RoundedCornerShape(18.dp)


                )
                {
                    Text(
                        "Got it",
                        fontFamily = poppinsFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//private fun HowToPrev() {
//    FructusTheme {
//        HowTo(
//            onClose = {}
//        )
//    }
//}

