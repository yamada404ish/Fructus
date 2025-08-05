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

data class SettingsState(
    val receiveNotifications: Boolean = false,
    val showSheet: Boolean = false,
    val showClearDialog: Boolean = false
)

class SettingsViewModel(
    private val context: Context,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeNotificationPref()
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


    fun onToggleNotifications(checked: Boolean) {
        viewModelScope.launch {
            val granted = isNotificationPermissionGranted(context)

            if (checked) {
                if (granted) {
                    dataStore.setReceiveNotifications(true)
                    _state.update {
                        it.copy(receiveNotifications = true, showSheet = false)
                    }
                } else {
                    // Show permission sheet, but don't update DataStore yet
                    _state.update {
                        it.copy(receiveNotifications = false, showSheet = true)
                    }
                }
            } else {
                dataStore.setReceiveNotifications(false)
                _state.update {
                    it.copy(receiveNotifications = false)
                }
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
}
