package com.example.fructus.ui.theme

import androidx.compose.ui.graphics.Color

data class AppColors(
    val main: Color,
    val accent: Color,
    val stroke: Color,
    val card: Color,
    val bg: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val ripenessStage: Color,
    val button: Color,
    val saveText: Color,

    val outerBox: Color,
    val innerBox: Color,

    val tip: Color,

    val dropdown: Color,

    val unripe: Color,
    val ripe: Color,
    val overripe: Color,
    val spoiled: Color,

    val ingr: Color


)

val LightAppColors = AppColors(
    main = Color(0xFF4CAF50),
    accent = Color(0xFFBADBA2), // notification card
    stroke = Color(0xFF718860),  // border notification card
    card = Color(0xFFFFFFFF), // fruit items / pop up
    bg = Color(0xFFF0EFE9), // background
    surface = Color(0xFFF0EFE9), // bottom nav bar
    textPrimary = Color.Black,
    textSecondary = Color(0xFF6B6767), // gray text
    textTertiary = Color(0xFF718860), // green text
    ripenessStage = Color(0xFF9AA08C),
    button = Color(0xFFBADBA2),
    saveText = Color(0xFF726F6F),

    outerBox = Color(0xFFE8E6D5),
    innerBox = Color(0xFFD1CEBA),

    tip = Color(0xFFC9DABE),

    dropdown = Color(0xFFE8E6D5),

    unripe = Color(0xFF75B148),
    ripe = Color(0xFFC9C618),
    overripe = Color(0xFFC59F13),
    spoiled = Color(0xFFF55D5D),

    ingr = Color(0xFFC9DABE)


)

val DarkAppColors = AppColors(
    main = Color(0xFF388E3C),
    accent = Color(0xFF3E443E), // notification card
    stroke = Color(0xFF6C7A6C),  // border notification card
    card = Color(0xFF313030), // fruit items / pop up
    bg = Color(0xFF121212), // background
    surface = Color(0xFF1F201F), // bottom nav bar
    textPrimary = Color.White,
    textSecondary = Color(0xFF979595), // gray text
    textTertiary = Color(0xFFA1C18A), // green text
    ripenessStage = Color(0xFF5E695E),
    button = Color(0xFF8FBF7B),
    saveText = Color(0xFFCDD5CF),

    outerBox = Color(0xFF1F201F),
    innerBox = Color(0xFF3E443E),

    tip = Color(0xFF3E443E),

    dropdown = Color(0xFF1F201F),

    unripe = Color(0xFF92E553),
    ripe = Color(0xFFE6E31C),
    overripe = Color(0xFFE97934),
    spoiled = Color(0xFFF55D5D),

    ingr = Color(0xFF8FBF7B)

)
