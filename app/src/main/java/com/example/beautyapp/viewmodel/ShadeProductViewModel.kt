package com.example.beautyapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.MakeupDatabase  // ← CORRECTED!
import com.example.beautyapp.data.MakeupProduct  // ← CORRECTED!
import com.example.beautyapp.data.Shade  // ← CORRECTED!
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShadeProductViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MakeupDatabase.getDatabase(application)
    private val shadeDao = db.shadeDao()
    private val productDao = db.productDao()

    private val _shades = MutableLiveData<List<Shade>>()
    val shades: LiveData<List<Shade>> = _shades

    private val _selectedShade = MutableLiveData<Shade?>()
    val selectedShade: LiveData<Shade?> = _selectedShade

    private val _products = MutableLiveData<List<MakeupProduct>>()
    val products: LiveData<List<MakeupProduct>> = _products

    init {
        loadShades()
    }

    private fun loadShades() {
        viewModelScope.launch(Dispatchers.IO) {
            val shadeList = shadeDao.getAllShades()
            _shades.postValue(shadeList)
        }
    }

    fun onShadeSelected(shade: Shade) {
        _selectedShade.value = shade
        viewModelScope.launch(Dispatchers.IO) {
            val result = productDao.getProductsForShade(shade.shadeId)
            _products.postValue(result)
        }
    }
}