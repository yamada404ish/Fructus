package com.example.fructus.ui.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fructus.util.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(private val dataStore: DataStoreManager) : ViewModel() {

    val isOnboardingCompleted = dataStore.onboardingCompleted
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.setOnboardingCompleted(true)
        }
    }
}
