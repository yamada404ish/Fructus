package com.example.fructus.ui.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArchiveViewModel(
    private val notificationDao: NotificationDao
) : ViewModel() {

    private val _archivedNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val archivedNotifications: StateFlow<List<NotificationEntity>> = _archivedNotifications

    init {
        // Load archived notifications
        viewModelScope.launch {
            notificationDao.getArchivedNotifications().collect { list ->
                _archivedNotifications.value = list
            }
        }
    }

    fun restoreNotification(notificationId: Int) {
        viewModelScope.launch {
            val notification = _archivedNotifications.value.find { it.id == notificationId }
            notification?.let {
                // Move back to active notifications
                notificationDao.updateNotification(it.copy(isArchived = false))
            }
        }
    }

    fun deleteArchivedNotification(notificationId: Int) {
        viewModelScope.launch {
            val notification = _archivedNotifications.value.find { it.id == notificationId }
            notification?.let {
                // You can add a delete method to DAO if needed
                // For now, we'll keep them archived
            }
        }
    }

    fun clearAllArchived() {
        viewModelScope.launch {
            // Delete all archived notifications
            _archivedNotifications.value.forEach { notification ->
                // Add delete functionality to DAO if needed
            }
        }
    }

    class ArchiveViewModelFactory(
        private val notificationDao: NotificationDao
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArchiveViewModel::class.java)) {
                return ArchiveViewModel(notificationDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}