package com.example.fructus.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.entity.FruitEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel(
    private val fruitDao: FruitDao
) : ViewModel() {

    fun saveFruit(name: String, ripeness: String, process: Boolean, confidence: Float, imagePath: String?) {
        viewModelScope.launch {
            val now = Date()
            val currentDate = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val timestamp = now.time

            val fruit = FruitEntity(
                name = name,
                ripeningStage = ripeness,
                ripeningProcess = process,
                confidence = confidence,
                scannedDate = currentDate,
                scannedTime = currentTime,
                scannedTimestamp = timestamp,
                imagePath = imagePath
            )
            fruitDao.insertFruit(fruit)
        }
    }
}

class CameraViewModelFactory(
    private val fruitDao: FruitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(fruitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
