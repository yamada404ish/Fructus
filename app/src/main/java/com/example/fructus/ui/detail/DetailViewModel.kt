package com.example.fructus.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.entity.FruitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailViewModel(
    private val fruitDao: FruitDao,
    private val fruitId: Int
) : ViewModel() {

    private val _fruit = MutableStateFlow<FruitEntity?>(null)
    val fruit: StateFlow<FruitEntity?> = _fruit

    init {
        viewModelScope.launch {
            fruitDao.getFruitById(fruitId).collectLatest { result ->
                _fruit.value = result
            }
        }
    }
}

class DetailViewModelFactory(
    private val fruitDao: FruitDao,
    private val fruitId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(fruitDao, fruitId) as T
    }
}
