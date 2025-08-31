package com.example.fructus.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao

class NotificationViewModelFactory(
    private val fruitDao: FruitDao,
    private val notificationDao: NotificationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(fruitDao, notificationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

