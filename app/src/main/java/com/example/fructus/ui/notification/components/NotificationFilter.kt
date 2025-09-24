package com.example.fructus.ui.notification.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.notification.model.Filter

@Composable
fun NotificationFilters(
    modifier: Modifier = Modifier,
    items: List<Filter>,
    selectedFilter: Filter,
    onSelectedFilter: (Filter) -> Unit
) {
    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items (items) { filter ->
            FilterChip(
                text = filter.name,
                isSelected = selectedFilter == filter,
                onClick = { onSelectedFilter(filter) },

            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}