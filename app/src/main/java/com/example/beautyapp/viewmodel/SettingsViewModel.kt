package com.example.beautyapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.ColorBlindMode
import com.example.beautyapp.data.FontSize
import com.example.beautyapp.data.Settings
import com.example.beautyapp.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel to manage settings state and handle user interactions
// Extends AndroidViewModel to access application context
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Create instance of SettingsDataStore to read/write preferences
    private val settingsDataStore = SettingsDataStore(application)

    // StateFlow that emits current settings to the UI
    // UI observes this and automatically updates when settings change
    val settings: StateFlow<Settings> = settingsDataStore.settingsFlow.stateIn(
        scope = viewModelScope,  // Tied to ViewModel lifecycle
        started = SharingStarted.WhileSubscribed(5000),  // Keep active while UI is subscribed + 5 seconds
        initialValue = Settings()  // Start with default settings until loaded from storage
    )

    // Function called when user toggles dark mode switch
    // Launches a coroutine to save the new preference asynchronously
    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateDarkMode(isDarkMode)  // Save to DataStore
        }
    }

    // Function called when user changes font size dropdown
    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            settingsDataStore.updateFontSize(fontSize)  // Save to DataStore
        }
    }

    // Function called when user changes color blind mode dropdown
    fun updateColorBlindMode(mode: ColorBlindMode) {
        viewModelScope.launch {
            settingsDataStore.updateColorBlindMode(mode)  // Save to DataStore
        }
    }

    // Function called when user toggles reduce animations switch
    fun updateReduceAnimations(reduce: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateReduceAnimations(reduce)  // Save to DataStore
        }
    }
}