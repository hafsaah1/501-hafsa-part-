package com.example.beautyapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beautyapp.data.Product
import com.example.beautyapp.ui.screens.*
import com.example.beautyapp.ui.components.BottomNavBar
import com.example.beautyapp.ui.theme.BeautyAppTheme
import com.example.beautyapp.viewmodel.MainViewModel
import com.example.beautyapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeautyAppTheme {
                BeautyApp(context = this)
            }
        }
    }
}

@Composable
fun BeautyApp(
    context: ComponentActivity,
    productViewModel: MainViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val productState by productViewModel.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    // Show product detail if a product is selected
    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct!!,
            isLiked = productState.likedProducts.contains(selectedProduct!!.id),
            onToggleLike = { productViewModel.toggleLike(it) },
            onAddToCart = {
                productViewModel.addToCart(it)
            },
            onBack = { selectedProduct = null }
        )
    } else {
        // Main app with navigation
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    activeTab = when(selectedTab) {
                        0 -> "home"
                        1 -> "search"
                        2 -> "scan"
                        3 -> "cart"
                        4 -> "profile"
                        else -> "home"
                    },
                    onTabChange = { tab ->
                        selectedTab = when(tab) {
                            "home" -> 0
                            "search" -> 1
                            "scan" -> 2
                            "cart" -> 3
                            "profile" -> 4
                            else -> 0
                        }
                    },
                    cartCount = productViewModel.getCartCount()
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedTab) {
                    0 -> {
                        // Home Tab - Weather Screen
                        WeatherScreen(
                            modifier = Modifier.fillMaxSize(),
                            context = context,
                            viewModel = weatherViewModel
                        )
                    }
                    1 -> {
                        // Products Tab
                        ProductsScreen(
                            products = productViewModel.getDisplayProducts(),
                            likedProducts = productState.likedProducts,
                            onToggleLike = { productViewModel.toggleLike(it) },
                            onAddToCart = { productViewModel.addToCart(it) },
                            loading = productState.loading,
                            brands = productState.availableBrands,
                            productTypes = productState.availableProductTypes,
                            selectedBrands = productState.selectedBrands,
                            selectedProductTypes = productState.selectedProductTypes,
                            onBrandToggle = { productViewModel.toggleBrandFilter(it) },
                            onProductTypeToggle = { productViewModel.toggleProductTypeFilter(it) },
                            onClearFilters = { productViewModel.clearFilters() },
                            hasActiveFilters = productViewModel.hasActiveFilters(),
                            onProductClick = { product -> selectedProduct = product }
                        )
                    }
                    2 -> {
                        // AR Scan Tab
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "AI Face Scan - Coming soon...")
                        }
                    }
                    3 -> {
                        // Cart Tab
                        CartScreen(
                            cartItems = productState.cartItems,
                            products = productState.products,
                            onAddToCart = { productViewModel.addToCart(it) },
                            onRemoveFromCart = { productViewModel.removeFromCart(it) }
                        )
                    }
                    4 -> {
                        // Profile Tab - Show Liked Products
                        ProductsScreen(
                            products = productState.products.filter { productState.likedProducts.contains(it.id) },
                            likedProducts = productState.likedProducts,
                            onToggleLike = { productViewModel.toggleLike(it) },
                            onAddToCart = { productViewModel.addToCart(it) },
                            loading = productState.loading,
                            brands = emptyList(),
                            productTypes = emptyList(),
                            selectedBrands = emptySet(),
                            selectedProductTypes = emptySet(),
                            onProductClick = { product -> selectedProduct = product }
                        )
                    }
                }
            }
        }
    }
}