package com.example.fructus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fructus.ui.screens.home.HomeScreen
import com.example.fructus.ui.theme.FructusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
//            FructusApp()
            FructusTheme {
                HomeScreen()
            }
        }
    }
}
