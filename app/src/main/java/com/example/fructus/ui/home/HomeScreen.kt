package com.example.fructus.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fructus.data.local.FruitDatabase
import com.example.fructus.navigation.Notification

@Composable
fun HomeScreen(
    navController: NavController,
    onFruitClick: (Int) -> Unit,

) {
    // Access the current context to get the database instance
    val context = LocalContext.current

    // Remember the Room database instance so it's not recreated on every recomposition
    val db = remember { FruitDatabase.getDatabase(context) }

    // Create HomeViewModel using a factory that injects the DAO
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(db.fruitDao())
    )

    // Observe the state from the ViewModel (list of fruits)
    val state by viewModel.state.collectAsState()

    // Delegate UI to the presentational component
    HomeScreenContent(
        state = state,
        onFruitClick = onFruitClick,
        onNotificationClick = { navController.navigate(Notification) },
    )
}

