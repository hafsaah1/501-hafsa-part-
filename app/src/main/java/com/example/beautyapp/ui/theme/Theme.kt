package com.example.beautyapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light theme colors - used when dark mode is OFF
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF472B6),        // Pink - main brand color
    secondary = Color(0xFFEC4899),       // Darker pink for accents
    tertiary = Color(0xFFFCE7F3),        // Very light pink for backgrounds
    background = Color(0xFFFFFFFF),      // Pure white background
    surface = Color(0xFFFFFFFF),         // White cards/surfaces
    onPrimary = Color.White,             // White text on pink buttons
    onSecondary = Color.White,           // White text on secondary elements
    onTertiary = Color(0xFF1F2937),      // Dark gray text on light pink
    onBackground = Color(0xFF1F2937),    // Dark gray text on white background
    onSurface = Color(0xFF1F2937),       // Dark gray text on white surfaces
    error = Color(0xFFEF4444),           // Red for errors
    onError = Color.White                // White text on error elements
)

// Dark theme colors - used when dark mode is ON
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF472B6),         // Pink stays same (brand color)
    secondary = Color(0xFFEC4899),       // Darker pink
    tertiary = Color(0xFF3B2533),        // Dark pink/purple for dark backgrounds
    background = Color(0xFF1F2937),      // Dark gray background
    surface = Color(0xFF374151),         // Medium gray for cards
    onPrimary = Color(0xFF1F2937),       // Dark text on pink in dark mode
    onSecondary = Color.White,           // White text on secondary
    onTertiary = Color(0xFFF3F4F6),      // Light gray text on dark pink
    onBackground = Color(0xFFF3F4F6),    // Light gray text on dark background
    onSurface = Color(0xFFF3F4F6),       // Light gray text on cards
    error = Color(0xFFEF4444),           // Red for errors
    onError = Color.White                // White text on errors
)

@Composable
fun BeautyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Use system setting by default
    content: @Composable () -> Unit
) {
    // Choose color scheme based on dark mode setting
    val colorScheme = if (darkTheme) {
        DarkColorScheme  // Use dark colors when dark mode is ON
    } else {
        LightColorScheme  // Use light colors when dark mode is OFF
    }

    // Update system bars (status bar, navigation bar) to match theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to match background
            window.statusBarColor = colorScheme.background.toArgb()
            // Make status bar icons dark in light mode, light in dark mode
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Apply the theme to all child composables
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // Defined in Type.kt
        content = content  // Your app content
    )
}