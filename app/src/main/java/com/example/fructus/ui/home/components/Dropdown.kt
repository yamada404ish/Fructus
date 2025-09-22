package com.example.fructus.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown() {

    val colors = MaterialTheme.appColors
    val menu = arrayOf("All", "Unripe", "Ripe", "Overripe", "Spoiled")
    val selectedMenu = remember { mutableStateOf(menu[0]) }
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
    ) {
        DisableSelection {
            TextField(
                value = selectedMenu.value,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = null,
                placeholder = null,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded.value)
                },
                modifier = Modifier
                    .menuAnchor()
                    .width(130.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                ),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor  = colors.card,
                    unfocusedContainerColor = colors.card,
                    disabledContainerColor = colors.card,
                    focusedTextColor  = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Transparent
                )
            )
        }

        Spacer(Modifier.height(62.dp))
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .width(130.dp)
                .background(
                    color = colors.card,
                    shape = RoundedCornerShape(16.dp)
                ),
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            menu
                .filter { it != selectedMenu.value }
                .forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    },
                    onClick = {
                        selectedMenu.value = item
                        expanded.value = false
                    },
                    modifier = Modifier.background(Color.Transparent)
                )
            }
        }

    }
}