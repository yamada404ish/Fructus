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
    fruitId: Int,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { FruitDatabase.getDatabase(context) }

    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(db.fruitDao(), fruitId)
    )

    val fruit = viewModel.fruit.collectAsState().value

    if (fruit == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        DetailScreenContent(fruit = fruit, onNavigate = onNavigate)
    }
}


