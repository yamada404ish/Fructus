package com.example.fructus.ui.shared

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitchButton(
    switchPadding: Dp = 3.dp,
    buttonWidth: Dp = 52.dp,
    buttonHeight: Dp = 26.dp,
    isChecked: Boolean,
    onCheckedChange:(Boolean) ->Unit
) {
   val switchSize by remember {
       mutableStateOf(buttonHeight-switchPadding*2)
   }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    var padding by remember {
        mutableStateOf(0.dp)
    }


    padding =  if (isChecked) buttonWidth - switchSize - switchPadding*2 else 0.dp

    val animateSize by animateDpAsState(
        if (isChecked) padding else 0.dp,
        tween(
            durationMillis = 700,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    Box(

        modifier = Modifier
            .width(buttonWidth)
            .height(buttonHeight)
            .clip(CircleShape)
            .background(if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme
                .colorScheme.secondary)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onCheckedChange(!isChecked)
            }
    ){

        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(switchPadding)
        ){

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(animateSize)
                    .background(Color.Transparent)
            )

            Box(
                modifier = Modifier
                    .size(switchSize)
                    .clip(CircleShape)
                    .background(Color.White)
            )

        }
    }
}
