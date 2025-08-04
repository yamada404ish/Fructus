package com.example.fructus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val fruitDao: FruitDao) : ViewModel() {

    // Backing state for UI (list of fruits)
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> get() = _state

    init {
        // Collect all fruits from Room and update UI state
        viewModelScope.launch {
            fruitDao.getAllFruits().collectLatest { fruits ->
                _state.value = HomeState(fruits = fruits)
            }
        }
    }
}

// Factory to inject DAO into ViewModel (used in Composable)
class HomeViewModelFactory(private val fruitDao: FruitDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(fruitDao) as T
    }
}

