package com.example.fructus.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fructus.util.DataStoreManager
import com.example.fructus.util.isNotificationPermissionGranted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents UI state for the Settings screen
data class SettingsState(
    val receiveNotifications: Boolean = false, // Whether notifications are allowed
    val showSheet: Boolean = false,            // Show bottom sheet to enable notifications
    val showClearDialog: Boolean = false       // Show confirmation dialog for clearing notifications
)

class SettingsViewModel(
    private val context: Context,
    private val dataStore: DataStoreManager
) : ViewModel() {

    // Backing state for the Settings screen
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        // Start observing user's notification preference when ViewModel is created
        observeNotificationPref()
    }

    // Listen to changes from DataStore and check if permission is granted
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

    // Called when user toggles the notification switch
    fun onToggleNotifications(checked: Boolean) {
        viewModelScope.launch {
            val granted = isNotificationPermissionGranted(context)

            if (checked) {
                if (granted) {
                    // Allow notifications in DataStore and update state
                    dataStore.setReceiveNotifications(true)
                    _state.update { it.copy(receiveNotifications = true, showSheet = false) }
                } else {
                    // Show bottom sheet if permission not granted
                    _state.update { it.copy(receiveNotifications = false, showSheet = true) }
                }
            } else {
                // User turned off notifications manually
                dataStore.setReceiveNotifications(false)
                _state.update { it.copy(receiveNotifications = false) }
            }
        }
    }

    // Hide the bottom sheet
    fun hideBottomSheet() {
        _state.update { it.copy(showSheet = false) }
    }

    // Show confirmation dialog to clear notifications
    fun showClearDialog() {
        _state.update { it.copy(showClearDialog = true) }
    }

    // Hide the clear notification dialog
    fun hideClearDialog() {
        _state.update { it.copy(showClearDialog = false) }
    }

    // Called after user enables notifications via system settings
    fun markReturnedFromSettings() {
        val granted = isNotificationPermissionGranted(context)
        if (granted) {
            viewModelScope.launch {
                dataStore.setReceiveNotifications(true)
            }
        }
        _state.update { it.copy(receiveNotifications = granted) }
    }
}
