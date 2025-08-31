package com.example.fructus.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.FruitNotification
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.mapper.toModel
import com.example.fructus.ui.notification.model.Filter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationViewModel(
    private val fruitDao: FruitDao
) : ViewModel() {

    var filter by mutableStateOf(Filter.All)
        private set

    private val _notifications = MutableStateFlow<List<FruitNotification>>(emptyList())
    val notifications: StateFlow<List<FruitNotification>> = _notifications

    init {
        viewModelScope.launch {
            fruitDao.getAllFruits().collect { fruits ->
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                val today = Date()

                val generated = fruits.mapNotNull { fruit ->
                    val scanned = dateFormat.parse(fruit.scannedDate) ?: return@mapNotNull null
                    val daysPassed = ((today.time - scanned.time) / (1000 * 60 * 60 * 24)).toInt()
                    val remaining = fruit.shelfLife - daysPassed

                    if (remaining == 1) {
                        FruitNotification(
                            fruit = fruit.toModel(), // map entity -> UI model
                            isRead = false
                        )
                    } else {
                        null
                    }
                }
                _notifications.value = generated
            }
        }
    }

    fun onSelectFilter(newFilter: Filter) {
        filter = newFilter
    }

    fun markNotificationAsRead(fruitId: Int) {
        _notifications.value = _notifications.value.map {
            if (it.fruit.id == fruitId) it.copy(isRead = true) else it
        }
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
