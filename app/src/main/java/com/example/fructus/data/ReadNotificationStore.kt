package com.example.fructus.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("read_notifications")

object ReadNotificationStore {
    private val READ_IDS_KEY = stringSetPreferencesKey("read_ids")

    fun readIds(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[READ_IDS_KEY] ?: emptySet()
        }
    }

    suspend fun markAsRead(context: Context, id: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[READ_IDS_KEY]?.toMutableSet() ?: mutableSetOf()
            current.add(id.toString())
            preferences[READ_IDS_KEY] = current
        }
    }

    suspend fun markAllAsRead(context: Context, ids: List<Int>) {
        context.dataStore.edit { preferences ->
            preferences[READ_IDS_KEY] = ids.map { it.toString() }.toSet()
        }
    }
}