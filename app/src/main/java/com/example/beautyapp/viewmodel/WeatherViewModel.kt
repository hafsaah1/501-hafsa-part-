package com.example.beautyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.weather.WeatherResponse
import com.example.beautyapp.network.WeatherApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Initial)
    val weatherState: StateFlow<WeatherState> = _weatherState

    fun fetchWeather(cityName: String, apiKey: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val weatherResponse = WeatherApiClient.apiService.getWeather(cityName, apiKey)
                _weatherState.value = WeatherState.Success(weatherResponse)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("404") == true -> "City not found"
                    e.message?.contains("401") == true -> "Invalid API key"
                    e.message?.contains("Network") == true -> "Network error. Please check your connection"
                    else -> e.message ?: "Unknown error"
                }
                _weatherState.value = WeatherState.Error(errorMessage)
            }
        }
    }

    fun setPreviousState(state: WeatherState) {
        _weatherState.value = state
    }

    sealed class WeatherState {
        object Initial : WeatherState()
        object Loading : WeatherState()
        data class Success(val weatherResponse: WeatherResponse) : WeatherState()
        data class Error(val errorMessage: String) : WeatherState()
    }
}