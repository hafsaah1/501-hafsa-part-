package com.example.beautyapp.data.weather

import com.squareup.moshi.Json

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)

// Helper functions
fun celsiusToFahrenheit(celsius: Double): Double {
    return (celsius * 9 / 5) + 32
}

fun getWeatherEmoji(description: String): String {
    return when {
        description.contains("clear", ignoreCase = true) -> "☀️"
        description.contains("cloud", ignoreCase = true) -> "☁️"
        description.contains("rain", ignoreCase = true) -> "🌧️"
        description.contains("snow", ignoreCase = true) -> "❄️"
        description.contains("storm", ignoreCase = true) -> "⛈️"
        description.contains("fog", ignoreCase = true) -> "🌫️"
        else -> "🌤️"
    }
}