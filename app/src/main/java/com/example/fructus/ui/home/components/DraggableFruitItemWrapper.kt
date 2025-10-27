package com.example.fructus.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.example.fructus.data.local.entity.FruitEntity
import kotlinx.coroutines.launch

@Composable
fun DraggableFruitItemWrapper(
    modifier: Modifier = Modifier,
    fruit: FruitEntity,
    isDragging: Boolean,
    onDragStart: (FruitEntity) -> Unit = {},
    onDrag: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var itemPosition by remember { mutableStateOf(Offset.Zero) }
    var originalPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                // Track the position of this item in the root coordinate space
                val position = coordinates.positionInRoot()
                if (!isDragging) {
                    originalPosition = position
                    itemPosition = position
                }
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart(fruit)
                        // Start dragging from the item's current position
                        onDrag(originalPosition)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Update the drag position based on cumulative drag
                        itemPosition = Offset(
                            itemPosition.x + dragAmount.x,
                            itemPosition.y + dragAmount.y
                        )
                        onDrag(itemPosition)
                    },
                    onDragEnd = {
                        // Immediately end dragging
                        onDragEnd()
                        // Reset position tracking
                        itemPosition = originalPosition
                    },
                    onDragCancel = {
                        onDragEnd()
                        itemPosition = originalPosition
                    }
                )
            }
    ) {
        // Hide content when dragging (it's shown in the overlay)
        if (!isDragging) {
            content()
        }


    }
}