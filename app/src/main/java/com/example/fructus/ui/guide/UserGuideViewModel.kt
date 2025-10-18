package com.example.fructus.ui.guide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fructus.ui.shared.model.Guide

class UserGuideViewModel : ViewModel() {

    var selectedGuide by mutableStateOf(Guide.Natural)
        private set

    fun onSelectGuide(newGuide: Guide) {
        selectedGuide = newGuide
    }
}
