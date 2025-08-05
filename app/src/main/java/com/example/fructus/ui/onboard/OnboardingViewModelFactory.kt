package com.example.fructus.ui.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fructus.util.DataStoreManager

class OnboardingViewModelFactory(
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingViewModel(dataStore) as T
    }
}
