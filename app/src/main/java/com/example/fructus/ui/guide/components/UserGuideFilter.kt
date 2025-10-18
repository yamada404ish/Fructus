package com.example.fructus.ui.guide.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.shared.FilterChip
import com.example.fructus.ui.shared.model.Guide

@Composable
fun UserGuideFilter(
    modifier: Modifier = Modifier,
    items: List<Guide>,
    selectedGuide: Guide,
    onSelectedGuide: (Guide) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(items) { guide ->
            FilterChip(
                text = guide.name,
                isSelected = selectedGuide == guide,
                onClick = { onSelectedGuide(guide) }
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}
