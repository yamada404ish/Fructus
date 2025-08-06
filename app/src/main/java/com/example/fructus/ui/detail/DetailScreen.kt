package com.example.fructus.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fructus.data.local.FruitDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    fruitId: Int,         // The ID of the fruit to show
    onNavigate: () -> Unit // Callback when user wants to go back
) {

    // Get database and DAO (Data Access Object)
    val context = LocalContext.current
    val db = remember { FruitDatabase.getDatabase(context) }

    // Create the DetailViewModel using the DAO and the fruit ID
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(db.fruitDao(), fruitId)
    )

    // Get the selected fruit from the ViewModel as state
    val fruit = viewModel.fruit.collectAsState().value

    if (fruit == null) {
        // While loading the fruit, show a spinner centered on screen
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // ⏳ Loading indicator
        }
    } else {
        // Once the fruit is loaded, show its details
        DetailScreenContent(fruit = fruit, onNavigate = onNavigate)
    }
}


/*
This screen is shown when a fruit is selected from the Home screen.

It uses the fruitId to fetch the fruit from the Room database.

Shows a loading spinner while waiting.

When the fruit is loaded, it passes it to DetailScreenContent to show the details.
*/


