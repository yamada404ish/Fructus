package com.example.fructus.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen size categories (common breakpoints)
 */
enum class ScreenType {
    SMALL, NORMAL, LARGE, XLARGE
}

/**
 * Get screen type based on width
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenType(): ScreenType {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    return when {
        screenWidth < 360 -> ScreenType.SMALL
        screenWidth in 360..399 -> ScreenType.NORMAL
        screenWidth in 400..599 -> ScreenType.LARGE
        else -> ScreenType.XLARGE
    }
}

/**
 * Get screen width in dp
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun screenWidthDp(): Int {
    return LocalConfiguration.current.screenWidthDp
}

/**
 * Get screen height in dp
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun screenHeightDp(): Int {
    return LocalConfiguration.current.screenHeightDp
}

/**
 * Set responsive dp value based on screen size
 */
@Composable
fun responsiveDp(small: Int, normal: Int, large: Int? = null): Dp {
    val type = getScreenType()
    return when (type) {
        ScreenType.SMALL -> small.dp
        ScreenType.LARGE, ScreenType.XLARGE -> (large ?: normal).dp
        else -> normal.dp
    }
}

/**
 * Set responsive sp value based on screen size
 */
@Composable
fun responsiveSp(small: Int, normal: Int, large: Int? = null): TextUnit {
    val type = getScreenType()
    return when (type) {
        ScreenType.SMALL -> small.sp
        ScreenType.LARGE, ScreenType.XLARGE -> (large ?: normal).sp
        else -> normal.sp
    }
}

/**
 * Quick helpers
 */
@Composable
fun isSmallScreen() = getScreenType() == ScreenType.SMALL

@Composable
fun isLargeScreen() =
    getScreenType() == ScreenType.LARGE || getScreenType() == ScreenType.XLARGE
