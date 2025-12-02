/*
 * MainViewModel.kt
 * PURPOSE: Central state management for BeautyApp - handles products, cart, favorites, notes, and filters
 * MAIN COMPONENTS: MainViewModel, AppState data class
 * KEY FEATURES:
 *   - Product fetching from Makeup API with 30s timeout
 *   - Cart management with shade selection support (CartItem with ProductColor)
 *   - Favorites/likes management via Room database
 *   - Notes management with image storage
 *   - Brand and product type filtering
 * DATA FLOW:
 *   - StateFlow<AppState> exposes reactive state to UI
 *   - Room DAOs for persistent storage (likes, notes)
 *   - Retrofit API for product data
 * STATE STRUCTURE:
 *   - products: All products from API
 *   - filteredProducts: Products after applying filters
 *   - cartItems: List<CartItem> (productId + quantity + selectedShade)
 *   - likedProducts: Set<Int> of product IDs
 *   - selectedBrands/selectedProductTypes: Active filters
 */

package com.example.beautyapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.AppDatabase
import com.example.beautyapp.data.CartItem
import com.example.beautyapp.data.LikedProduct
import com.example.beautyapp.data.Note
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.ProductColor
import com.example.beautyapp.network.MakeupApiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

data class AppState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val likedProducts: Set<Int> = emptySet(),
    val cartItems: List<CartItem> = emptyList(),  // UPDATED - now List<CartItem> instead of Map!
    val loading: Boolean = false,
    val activeTab: String = "home",
    val selectedBrands: Set<String> = emptySet(),
    val selectedProductTypes: Set<String> = emptySet(),
    val availableBrands: List<String> = emptyList(),
    val availableProductTypes: List<String> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val likedProductDao = AppDatabase.getDatabase(application).likedProductDao()
    private val noteDao = AppDatabase.getDatabase(application).noteDao()

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    val notes = noteDao.getAllNotes()

    // Updated with timeout configuration
    private val api: MakeupApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 30 second connection timeout
            .readTimeout(30, TimeUnit.SECONDS)     // 30 second read timeout
            .writeTimeout(30, TimeUnit.SECONDS)    // 30 second write timeout
            .build()

        Retrofit.Builder()
            .baseUrl("https://makeup-api.herokuapp.com/")
            .client(okHttpClient)  // Attach the custom OkHttp client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MakeupApiService::class.java)
    }

    init {
        fetchProducts()

        viewModelScope.launch {
            likedProductDao.getAllLikedProductIds().collect { likedIds ->
                _state.update { it.copy(likedProducts = likedIds.toSet()) }
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                val products = api.getProducts()

                val brands = products.mapNotNull { it.brand }.distinct().sorted()
                val productTypes = products.mapNotNull { it.productType }.distinct().sorted()

                _state.value = _state.value.copy(
                    products = products,
                    filteredProducts = products,
                    loading = false,
                    availableBrands = brands,
                    availableProductTypes = productTypes
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false)
                Log.e("MainViewModel", "Failed to fetch products", e)
            }
        }
    }

    fun toggleBrandFilter(brand: String) {
        val currentBrands = _state.value.selectedBrands.toMutableSet()
        if (currentBrands.contains(brand)) {
            currentBrands.remove(brand)
        } else {
            currentBrands.add(brand)
        }
        _state.value = _state.value.copy(selectedBrands = currentBrands)
        applyFilters()
    }

    fun toggleProductTypeFilter(productType: String) {
        val currentTypes = _state.value.selectedProductTypes.toMutableSet()
        if (currentTypes.contains(productType)) {
            currentTypes.remove(productType)
        } else {
            currentTypes.add(productType)
        }
        _state.value = _state.value.copy(selectedProductTypes = currentTypes)
        applyFilters()
    }

    fun clearFilters() {
        _state.value = _state.value.copy(
            selectedBrands = emptySet(),
            selectedProductTypes = emptySet(),
            filteredProducts = _state.value.products
        )
    }

    private fun applyFilters() {
        val filtered = _state.value.products.filter { product ->
            val brandMatch = _state.value.selectedBrands.isEmpty() ||
                    _state.value.selectedBrands.contains(product.brand)
            val typeMatch = _state.value.selectedProductTypes.isEmpty() ||
                    _state.value.selectedProductTypes.contains(product.productType)
            brandMatch && typeMatch
        }
        _state.value = _state.value.copy(filteredProducts = filtered)
    }

    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            val currentLikes = _state.value.likedProducts
            if (currentLikes.contains(productId)) {
                likedProductDao.unlikeProduct(LikedProduct(id = productId))
            } else {
                likedProductDao.likeProduct(LikedProduct(id = productId))
            }
        }
    }

    fun addNote(title: String, content: String, imagePath: String?) {
        viewModelScope.launch {
            val note = Note(
                id = UUID.randomUUID().toString(),
                title = title,
                content = content,
                imagePath = imagePath
            )
            noteDao.insertNote(note)
        }
    }

    fun deleteNote(noteId: String, imagePath: String?) {
        viewModelScope.launch {
            imagePath?.let { path ->
                try {
                    File(path).delete()
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Failed to delete image file", e)
                }
            }
            noteDao.deleteNoteById(noteId)
        }
    }

    // UPDATED - Add to cart with shade support
    fun addToCart(productId: Int, selectedShade: ProductColor? = null) {
        val currentItems = _state.value.cartItems.toMutableList()

        // Find if this exact product + shade combo already exists
        val existingIndex = currentItems.indexOfFirst {
            it.productId == productId && it.selectedShade == selectedShade
        }

        if (existingIndex >= 0) {
            // Same product + same shade = increase quantity
            currentItems[existingIndex] = currentItems[existingIndex].copy(
                quantity = currentItems[existingIndex].quantity + 1
            )
        } else {
            // New product or different shade = add new item
            currentItems.add(CartItem(productId, 1, selectedShade))
        }

        _state.value = _state.value.copy(cartItems = currentItems)
    }

    // UPDATED - Remove from cart with shade support
    fun removeFromCart(productId: Int, selectedShade: ProductColor? = null) {
        val currentItems = _state.value.cartItems.toMutableList()

        // Find the exact product + shade combo
        val existingIndex = currentItems.indexOfFirst {
            it.productId == productId && it.selectedShade == selectedShade
        }

        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            if (item.quantity > 1) {
                // Decrease quantity
                currentItems[existingIndex] = item.copy(quantity = item.quantity - 1)
            } else {
                // Remove item completely
                currentItems.removeAt(existingIndex)
            }
            _state.value = _state.value.copy(cartItems = currentItems)
        }
    }

    fun setActiveTab(tab: String) {
        _state.value = _state.value.copy(activeTab = tab)
    }

    // UPDATED - Calculate total items in cart
    fun getCartCount(): Int {
        return _state.value.cartItems.sumOf { it.quantity }
    }

    fun getFeaturedProducts(): List<Product> {
        val products = _state.value.products
        val foundation = products.firstOrNull { it.productType == "foundation" }
        val blush = products.firstOrNull { it.productType == "blush" }
        val lipstick = products.firstOrNull { it.productType == "lipstick" }
        return listOfNotNull(foundation, blush, lipstick)
    }

    fun getDisplayProducts(): List<Product> {
        return _state.value.filteredProducts
    }

    fun hasActiveFilters(): Boolean {
        return _state.value.selectedBrands.isNotEmpty() ||
                _state.value.selectedProductTypes.isNotEmpty()
    }
}