package com.example.fructus.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.R
import com.example.fructus.ui.theme.FructusTheme
import com.example.fructus.ui.theme.appColors
import com.example.fructus.ui.theme.poppinsFontFamily


@Composable
fun Dropdown(
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.appColors
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Anchor (dropdown button)
        Row(
            modifier = Modifier
                .width(110.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.dropdown)
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedItem,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = colors.textPrimary,
                maxLines = 1
            )
            Icon(
                painter = painterResource(R.drawable.dropdown),
                contentDescription = "Dropdown",
                modifier = Modifier
                    .size(14.dp)
                    .graphicsLayer(rotationZ = if (expanded) 0f else 180f),
                tint = Color(0xFF718860)
            )
        }

        Spacer(Modifier.height(52.dp))

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
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
            items.forEach { item ->
                if (item != selectedItem) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = colors.textPrimary,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .heightIn(min = 34.dp)
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DropdownPrev() {
    FructusTheme {
        Dropdown(
            selectedItem = "Overripe",
            items = listOf("All", "Unripe", "Ripe", "Overripe", "Spoiled"),
            onItemSelected = {}
        )
    }
}
