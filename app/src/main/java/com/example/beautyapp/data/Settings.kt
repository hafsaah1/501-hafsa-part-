package com.example.beautyapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Settings data class - represents the user's accessibility preferences
data class Settings(
    val isDarkMode: Boolean = false,  // true = dark theme, false = light theme
    val fontSize: FontSize = FontSize.MEDIUM,  // default font size is medium
    val colorBlindMode: ColorBlindMode = ColorBlindMode.NONE,  // no color adjustments by default
    val reduceAnimations: Boolean = false  // animations enabled by default
)

// Font size options
enum class FontSize {
    SMALL,    // 12-14sp
    MEDIUM,   // 14-16sp (default)
    LARGE     // 16-18sp
}

// Color blind mode options
enum class ColorBlindMode {
    NONE,         // No color filter (default)
    PROTANOPIA,   // Red-weak color vision
    DEUTERANOPIA  // Green-weak color vision
}

// Extension property to create a DataStore instance
// This stores user preferences in a file called "settings" on the device
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Class responsible for reading and writing settings to persistent storage
// Uses DataStore (modern alternative to SharedPreferences)
class SettingsDataStore(private val context: Context) {

    companion object {
        // Keys used to store each setting in DataStore
        // These act like dictionary keys to save/retrieve values
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")  // Stores true/false for dark mode
        private val FONT_SIZE_KEY = stringPreferencesKey("font_size")  // Stores "SMALL", "MEDIUM", or "LARGE"
        private val COLOR_BLIND_MODE_KEY = stringPreferencesKey("color_blind_mode")  // Stores "NONE", "PROTANOPIA", etc.
        private val REDUCE_ANIMATIONS_KEY = booleanPreferencesKey("reduce_animations")  // Stores true/false for animations
    }

    // Flow that emits the current settings whenever they change
    // This allows the UI to automatically update when settings are modified
    val settingsFlow: Flow<Settings> = context.dataStore.data.map { preferences ->
        Settings(
            // Read each setting from DataStore, using default values if not found
            isDarkMode = preferences[DARK_MODE_KEY] ?: false,  // Default: light mode
            fontSize = FontSize.valueOf(preferences[FONT_SIZE_KEY] ?: "MEDIUM"),  // Default: medium font
            colorBlindMode = ColorBlindMode.valueOf(preferences[COLOR_BLIND_MODE_KEY] ?: "NONE"),  // Default: no filter
            reduceAnimations = preferences[REDUCE_ANIMATIONS_KEY] ?: false  // Default: animations on
        )
    }

    // Function to save dark mode preference
    // suspend = can only be called from a coroutine (async operation)
    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode  // Write new value to storage
        }
    }

    // Function to save font size preference
    suspend fun updateFontSize(fontSize: FontSize) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize.name  // Save enum name as string (e.g., "LARGE")
        }
    }

    // Function to save color blind mode preference
    suspend fun updateColorBlindMode(mode: ColorBlindMode) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_BLIND_MODE_KEY] = mode.name  // Save enum name as string
        }
    }

    // Function to save animation preference
    suspend fun updateReduceAnimations(reduce: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REDUCE_ANIMATIONS_KEY] = reduce  // Write new value to storage
        }
    }
}