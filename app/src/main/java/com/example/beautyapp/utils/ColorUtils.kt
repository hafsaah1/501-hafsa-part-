package com.example.beautyapp.utils

import androidx.compose.ui.graphics.Color

fun parseHexColor(hexString: String?): Color {
    if (hexString.isNullOrEmpty()) return Color.Gray

    return try {
        val cleanHex = hexString.removePrefix("#")
        val colorInt = cleanHex.toLong(16)

        if (cleanHex.length == 6) {
            Color(0xFF000000 or colorInt)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        Color.Gray
    }
}