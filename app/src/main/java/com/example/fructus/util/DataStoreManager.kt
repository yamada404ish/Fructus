package com.example.fructus.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fructus_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val RECEIVE_NOTIFICATIONS_KEY = booleanPreferencesKey("receive_notifications")
        private val SHOULD_REQUEST_NOTIFICATION_KEY = booleanPreferencesKey("should_request_notification")
    }

    // Onboarding flow
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = value
        }
    }

    // Receive notifications flow (used in settings)
    val receiveNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[RECEIVE_NOTIFICATIONS_KEY] ?: true }

    suspend fun setReceiveNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RECEIVE_NOTIFICATIONS_KEY] = enabled
        }
    }

    // Should request permission flow (used for home screen logic)
    val shouldRequestNotificationFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SHOULD_REQUEST_NOTIFICATION_KEY] ?: false }

    suspend fun setRequestNotificationPermission(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOULD_REQUEST_NOTIFICATION_KEY] = value
        }
    }


}
