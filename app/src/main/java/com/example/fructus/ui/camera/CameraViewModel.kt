package com.example.fructus.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.entity.FruitEntity
import kotlinx.coroutines.launch

class CameraViewModel(private val fruitDao: FruitDao) : ViewModel() {

    fun saveFruit(name: String, ripeness: String) {
        viewModelScope.launch {
            // ✅ Insert placeholder values for now
            val fruit = FruitEntity(
                name = name,
                shelfLife = 3, // placeholder
                ripeningStage = ripeness,
                ripeningProcess = false, // placeholder
            )
            fruitDao.insertFruit(fruit)
        }
    }
}

class CameraViewModelFactory(private val fruitDao: FruitDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(fruitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

