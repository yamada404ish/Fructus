package com.example.fructus.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.fructus.navigation.FructusNav
import com.example.fructus.ui.theme.FructusTheme


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FructusApp(
    shouldOpenNotifications: Boolean = false,
    targetFruitId: Int? = null
) {
    FructusTheme {
        FructusNav(
            shouldOpenNotifications = shouldOpenNotifications,
            targetFruitId = targetFruitId
        )
    }
}
