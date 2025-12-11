package com.example.fructus.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single instance of DataStore using the name "fructus_prefs"
private val Context.dataStore by preferencesDataStore(name = "fructus_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val RECEIVE_NOTIFICATIONS_KEY = booleanPreferencesKey("receive_notifications")
        private val SHOULD_REQUEST_NOTIFICATION_KEY = booleanPreferencesKey("should_request_notification")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        private val SCAN_WARNING_SHOWN_KEY = booleanPreferencesKey("scan_warning_shown")
    }

    // --- ONBOARDING ---
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = value
        }
    }

    // --- NOTIFICATION TOGGLE ---
    val receiveNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[RECEIVE_NOTIFICATIONS_KEY] ?: true }

    suspend fun setReceiveNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RECEIVE_NOTIFICATIONS_KEY] = enabled
        }
    }

    // --- PERMISSION REQUEST CONTROL ---
    val shouldRequestNotificationFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SHOULD_REQUEST_NOTIFICATION_KEY] ?: true }

    suspend fun setRequestNotificationPermission(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOULD_REQUEST_NOTIFICATION_KEY] = value
        }
    }

    // --- DARK MODE ---
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false } // Default to light mode

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    val scanWarningShownFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SCAN_WARNING_SHOWN_KEY] ?: false }

    suspend fun setScanWarningShown(shown: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SCAN_WARNING_SHOWN_KEY] = shown
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}