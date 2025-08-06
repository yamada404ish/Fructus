package com.example.fructus.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single instance of DataStore using the name "fructus_prefs"
// This is an extension property on Context
private val Context.dataStore by preferencesDataStore(name = "fructus_prefs")

// DataStoreManager handles reading/writing simple key-value data
class DataStoreManager(private val context: Context) {

    companion object {
        // Keys used to access values in the DataStore
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val RECEIVE_NOTIFICATIONS_KEY = booleanPreferencesKey("receive_notifications")
        private val SHOULD_REQUEST_NOTIFICATION_KEY = booleanPreferencesKey("should_request_notification")
    }

    // --- ONBOARDING ---

    // Flow that emits whether the onboarding screen has been completed
    // Defaults to false if no value is stored
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    // Saves the onboarding completed state (true/false)
    suspend fun setOnboardingCompleted(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = value
        }
    }

    // --- NOTIFICATION TOGGLE (FROM SETTINGS) ---

    // Flow that emits the user's preference to receive notifications
    // Defaults to true (enabled) if not set yet
    val receiveNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[RECEIVE_NOTIFICATIONS_KEY] ?: true }

    // Saves the user's notification toggle preference
    suspend fun setReceiveNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RECEIVE_NOTIFICATIONS_KEY] = enabled
        }
    }

    // --- PERMISSION REQUEST CONTROL ---

    // Flow that tracks whether we should ask the user for notification permission
    // Used to avoid showing the prompt repeatedly
    val shouldRequestNotificationFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SHOULD_REQUEST_NOTIFICATION_KEY] ?: false }

    // Updates the state of whether to request notification permission again
    suspend fun setRequestNotificationPermission(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOULD_REQUEST_NOTIFICATION_KEY] = value
        }
    }
}
