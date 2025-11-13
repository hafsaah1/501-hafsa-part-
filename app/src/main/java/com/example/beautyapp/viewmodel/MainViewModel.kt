package com.example.beautyapp.viewmodel

import android.app.Application // <-- 1. IMPORT Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel // <-- 2. CHANGE from ViewModel to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.AppDatabase // <-- 3. IMPORT Database
import com.example.beautyapp.data.LikedProduct // <-- 4. IMPORT LikedProduct Entity
import com.example.beautyapp.data.Product
import com.example.beautyapp.network.MakeupApiService // <-- Make sure this is your correct service
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// This data class is the same
data class AppState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val likedProducts: Set<Int> = emptySet(), // This will now come from the DB
    val cartItems: Map<Int, Int> = emptyMap(),
    val loading: Boolean = false,
    val activeTab: String = "home",
    val selectedBrands: Set<String> = emptySet(),
    val selectedProductTypes: Set<String> = emptySet(),
    val availableBrands: List<String> = emptyList(),
    val availableProductTypes: List<String> = emptyList()
)

// <-- 5. CHANGE signature to (application: Application) and extend AndroidViewModel(application)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // <-- 6. GET the DAO from the database
    private val likedProductDao = AppDatabase.getDatabase(application).likedProductDao()

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    // Assuming you have your MakeupApiService defined somewhere else
    // If not, use the code you had before to create the 'api' service
    private val api: MakeupApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://makeup-api.herokuapp.com/") // <-- FIXED
            .addConverterFactory(GsonConverterFactory.create()) //added//           .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MakeupApiService::class.java)
    }

    init {
        fetchProducts()

        // <-- 7. NEW: Launch a coroutine to listen to the database
        // This Flow automatically updates the state whenever the database changes
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
                val products = api.getProducts() // Use your Retrofit instance

                // Extract unique brands and product types
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

    // <-- 8. UPDATED: This function now writes to the database
    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            val currentLikes = _state.value.likedProducts
            if (currentLikes.contains(productId)) {
                // Unlike: Delete from database
                likedProductDao.unlikeProduct(LikedProduct(id = productId))
            } else {
                // Like: Insert into database
                likedProductDao.likeProduct(LikedProduct(id = productId))
            }
            // The Flow in init{} will automatically update the state,
            // so no need to update _state here.
        }
    }

    // --- (The rest of your functions stay the same) ---

    fun addToCart(productId: Int) {
        val currentCart = _state.value.cartItems.toMutableMap()
        val currentQty = currentCart[productId] ?: 0
        currentCart[productId] = currentQty + 1
        _state.value = _state.value.copy(cartItems = currentCart)
    }

    fun removeFromCart(productId: Int) {
        val currentCart = _state.value.cartItems.toMutableMap()
        val currentQty = currentCart[productId] ?: 0
        if (currentQty <= 1) {
            currentCart.remove(productId)
        } else {
            currentCart[productId] = currentQty - 1
        }
        _state.value = _state.value.copy(cartItems = currentCart)
    }

    fun setActiveTab(tab: String) {
        _state.value = _state.value.copy(activeTab = tab)
    }

    fun getCartCount(): Int {
        return _state.value.cartItems.values.sum()
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