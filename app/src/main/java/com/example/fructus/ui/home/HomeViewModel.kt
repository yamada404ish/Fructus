//package com.example.fructus.home
//
//import androidx.lifecycle.ViewModel
//import com.example.fructus.data.DummyFruitDataSource
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//
//class HomeViewModel : ViewModel() {
//
//    private val _state = MutableStateFlow(HomeState())
//    val state: StateFlow<HomeState> = _state
//
//    init {
//        loadFruits()
//    }
//
//    private fun loadFruits() {
//        val fruits = DummyFruitDataSource.fruitList
//        _state.value = HomeState(fruits = fruits)
//    }
//}
