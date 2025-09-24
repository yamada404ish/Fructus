package com.example.fructus.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    selectedMenu: String,
    onMenuSelected: (String) -> Unit
) {
    val colors = MaterialTheme.appColors
    val menu = arrayOf("All", "Unripe", "Ripe", "Overripe", "Spoiled")
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
    ) {
        TextField(
            value = selectedMenu,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                if (expanded.value) {
                    Icon(
                        painter = painterResource(R.drawable.dropdown) ,
                        contentDescription = "Selected",
                        modifier = Modifier
                            .size(12.dp),
                        tint = Color(0xFF718860)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.dropdown) ,
                        contentDescription = "Selected",
                        modifier = Modifier
                            .graphicsLayer(rotationZ = 180f)
                            .size(12.dp),
                        tint = Color(0xFF718860)
                    )
                }
            },
            modifier = Modifier
                .menuAnchor()
                .width(122.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp)),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                fontSize = 14.sp
            ),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = colors.dropdown,
                unfocusedContainerColor = colors.dropdown,
                disabledContainerColor = colors.dropdown,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(62.dp))

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .width(122.dp)
                .background(
                    color = colors.dropdown,
                    shape = RoundedCornerShape(16.dp)
                ),
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            menu
            .filter { it != selectedMenu }
            .forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        onMenuSelected(item)
                        expanded.value = false
                    },
                    modifier = Modifier.background(Color.Transparent)
                )
            }
        }
    }
}

