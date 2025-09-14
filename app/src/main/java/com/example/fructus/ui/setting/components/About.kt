package com.example.fructus.ui.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.poppinsFontFamily

@Composable
fun About() {
    // Define your team structure
    val teamSections = listOf(
        "Backend" to listOf("Adrian Miclat | Stephen Ortega", "Jenevieve Tonion"),
        "Frontend" to listOf("Jenevieve Tonion | Hannah Quinto"),
        "Resources" to listOf("Missy Tanhueco | Michael Sazon")
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Title
            Text(
                "Development Team",
                fontFamily = poppinsFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Loop through each section
            teamSections.forEach { (role, members) ->
                Text(
                    role,
                    fontFamily = poppinsFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                members.forEach { member ->
                    Text(
                        member,
                        fontFamily = poppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                "Developed in Android Studio",
                fontFamily = poppinsFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}


@Preview
@Composable
private fun AboutPrev() {
    FructusTheme {
        About()
    }
}