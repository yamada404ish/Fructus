package com.example.fructus.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.dao.NotificationDao
import com.example.fructus.util.DataStoreManager
import com.example.fructus.util.isNotificationPermissionGranted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Updated SettingsState with dark mode
data class SettingsState(
    val receiveNotifications: Boolean = false,
    val isDarkMode: Boolean = false, // New dark mode state
    val showSheet: Boolean = false,
    val showClearDialog: Boolean = false,
    val navigateToOnboardingPreview: Boolean = false,
    val navigateToFreshStart: Boolean = false
)

class SettingsViewModel(
    private val context: Context,
    private val dataStore: DataStoreManager,
    private val notificationDao: NotificationDao,
    private val fruitDao: FruitDao
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeNotificationPref()
        observeDarkModePref() // New observer for dark mode
    }

    private fun observeNotificationPref() {
        viewModelScope.launch {
            dataStore.receiveNotificationsFlow.collect { enabled ->
                val granted = isNotificationPermissionGranted(context)
                _state.update {
                    it.copy(receiveNotifications = enabled && granted)
                }
            }
        }
    }

    // New function to observe dark mode preference
    private fun observeDarkModePref() {
        viewModelScope.launch {
            dataStore.darkModeFlow.collect { isDark ->
                _state.update {
                    it.copy(isDarkMode = isDark)
                }
            }
        }
    }

    // New function to toggle dark mode
    fun onToggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setDarkMode(enabled)
        }
    }

    fun onToggleNotifications(checked: Boolean) {
        viewModelScope.launch {
            val granted = isNotificationPermissionGranted(context)

            if (checked) {
                if (granted) {
                    dataStore.setReceiveNotifications(true)
                    _state.update { it.copy(receiveNotifications = true, showSheet = false) }
                } else {
                    _state.update { it.copy(receiveNotifications = false, showSheet = true) }
                }
            } else {
                dataStore.setReceiveNotifications(false)
                _state.update { it.copy(receiveNotifications = false) }
            }
        }
    }

    fun hideBottomSheet() {
        _state.update { it.copy(showSheet = false) }
    }

    fun showClearDialog() {
        _state.update { it.copy(showClearDialog = true) }
    }

    fun hideClearDialog() {
        _state.update { it.copy(showClearDialog = false) }
    }

    fun markReturnedFromSettings() {
        val granted = isNotificationPermissionGranted(context)
        if (granted) {
            viewModelScope.launch {
                dataStore.setReceiveNotifications(true)
            }
        }
        _state.update { it.copy(receiveNotifications = granted) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            notificationDao.clearAll()
            fruitDao.clearAll()
            dataStore.clearAll()
            dataStore.setOnboardingCompleted(false)

            _state.update {
                it.copy(
                    receiveNotifications = false,
                    isDarkMode = false, // Reset dark mode to default
                    showClearDialog = false,
                    navigateToFreshStart = true
                )
            }
        }
    }

    fun showOnboarding() {
        _state.update {
            it.copy(navigateToOnboardingPreview = true)
        }
    }

    fun resetPreviewNavigateFlag() {
        _state.update { it.copy(navigateToOnboardingPreview = false) }
    }

    fun resetFreshStartNavigateFlag() {
        _state.update { it.copy(navigateToFreshStart = false) }
    }
}