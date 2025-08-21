//package com.example.fructus
//
// import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.Notifications
//import androidx.compose.material.icons.outlined.Settings
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.FloatingActionButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.fructus.ui.theme.FructusTheme
//
//@Composable
//fun ScannerScreen() {
//    var selectedItem by remember { mutableIntStateOf(0) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF0F0F0))
//    ) {
//        // Main content area
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(bottom = 80.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "No fruits yet, start scanning!",
//                fontSize = 18.sp,
//                color = Color(0xFF666666),
//                fontWeight = FontWeight.Normal,
//                textAlign = TextAlign.Center
//            )
//        }
//
//        // Bottom navigation with curved design and FAB - positioned at bottom
//        Box(
//            modifier = Modifier.align(Alignment.BottomCenter)
//        ) {
//            CurvedBottomNavWithFAB(
//                selectedItem = selectedItem,
//                onItemSelected = { selectedItem = it },
//                onFABClick = {
//                    // Handle scan action
//                }
//            )
//        }
//    }
//}
//
//
//
//
//
//@Preview
//@Composable
//private fun ScanPrev() {
//    FructusTheme {
//        ScannerScreen()
//    }
//}
//
//// Required imports:
//
//
