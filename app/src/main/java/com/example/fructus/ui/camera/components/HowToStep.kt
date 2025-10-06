package com.example.fructus.ui.camera.components

import com.example.fructus.R

data class HowToStep(
    val title: String,
    val subtitle: String,
    val tutorialImg: Int,
    val iconRes: Int
)

val howToSteps = listOf(
    HowToStep(
        title = "Keep the fruit inside \nthe box",
        subtitle = "Make sure the entire fruit is captured within the box.",
        tutorialImg = R.drawable.how_to1,
        iconRes = R.drawable.one
    ),
    HowToStep(
        title = "Check if the fruit has enough light",
        subtitle = "Make sure fruit is well lit, if not use the flashlight",
        tutorialImg = R.drawable.how_to2,
        iconRes = R.drawable.two
    ),
    HowToStep(
        title = "Press scan to capture the fruit",
        subtitle = "Make sure to press scan to capture the fruit properly.",
        tutorialImg = R.drawable.how_to3,
        iconRes = R.drawable.three
    )
)
