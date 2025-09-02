package com.example.fructus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.entity.FruitEntity
import com.example.fructus.ui.home.model.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Holds the list of fruits to be shown on the Home screen
data class HomeState(
    val fruits: List<FruitEntity> = emptyList(),
    val isLoading: Boolean = true,
    val sortOrder: SortOrder = SortOrder.NEWEST
)

// ViewModel handles business logic and provides data to the Home screen
class HomeViewModel(private val fruitDao: FruitDao) : ViewModel() {

    // Private mutable state (can only be changed inside ViewModel)
    private val _state = MutableStateFlow(HomeState(isLoading = true))

    // Public read-only version exposed to the UI
    val state: StateFlow<HomeState> get() = _state


    private var allFruits: List<FruitEntity> = emptyList()

    // When ViewModel is created
    init {
        viewModelScope.launch {
            fruitDao.getAllFruits().collectLatest { fruits ->
                allFruits = fruits
                sortFruits() // Apply initial sort
            }
        }
    }

    fun toggleSortOrder() {
        val newOrder = if (_state.value.sortOrder == SortOrder.NEWEST) SortOrder.OLDEST else SortOrder.NEWEST
        val sorted = when (newOrder) {
            SortOrder.NEWEST -> allFruits.sortedByDescending { it.scannedTimestamp }
            SortOrder.OLDEST -> allFruits.sortedBy { it.scannedTimestamp }
        }
        _state.value = _state.value.copy(fruits = sorted, sortOrder = newOrder)
    }

    private fun sortFruits() {
        val currentOrder = _state.value.sortOrder
        val sorted = when (currentOrder) {
            SortOrder.NEWEST -> allFruits.sortedByDescending { it.scannedTimestamp }
            SortOrder.OLDEST -> allFruits.sortedBy { it.scannedTimestamp }
        }
        _state.value = _state.value.copy(fruits = sorted, isLoading = false)
    }


}

// Factory used to create HomeViewModel and inject the FruitDao dependency
class HomeViewModelFactory(
    private val fruitDao: FruitDao // This comes from the Room database
) : ViewModelProvider.Factory {

    // Creates the ViewModel when it's needed in a Composable
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(fruitDao) as T // Safe cast to expected type
    }
}






/*
HomeViewModel observes all fruits from Room using Flow.

It stores the result in HomeState, which the UI collects using state.

A HomeViewModelFactory is used to provide the FruitDao dependency when creating the ViewModel.
*/