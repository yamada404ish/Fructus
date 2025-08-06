package com.example.fructus.ui.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.util.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel for handling onboarding logic and state
class OnboardingViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {

    // Flow that tracks whether onboarding is completed
    val isOnboardingCompleted = dataStore.onboardingCompleted
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Called when user completes onboarding
    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.setOnboardingCompleted(true)
        }
    }

    // Called to request notification permission (only once)
    fun setRequestNotificationOnce() {
        viewModelScope.launch {
            dataStore.setRequestNotificationPermission(true)
        }
    }
}

// Factory to inject DataStore into OnboardingViewModel
class OnboardingViewModelFactory(
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingViewModel(dataStore) as T
    }
}
