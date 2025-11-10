package com.example.beautyapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beautyapp.data.Product
import com.example.beautyapp.ui.screens.*
import com.example.beautyapp.ui.components.BottomNavBar
import com.example.beautyapp.ui.theme.BeautyAppTheme
import com.example.beautyapp.viewmodel.MainViewModel
import com.example.beautyapp.viewmodel.WeatherViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeautyAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Login Screen
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { userName ->
                                Log.d("MainActivity", "onLoginSuccess called with: $userName")
                                navController.navigate("main/$userName") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = {
                                navController.navigate("signup")
                            }
                        )
                    }

                    // Sign Up Screen
                    composable("signup") {
                        SignUpScreen(
                            onSignUpSuccess = {
                                navController.popBackStack()
                            },
                            onLoginClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // Main Beauty App (after login)
                    composable("main/{userName}") { backStackEntry ->
                        val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                        Log.d("MainActivity", "Navigated to main screen with: $userName")

                        BeautyApp(
                            context = this@MainActivity,
                            userName = userName,
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BeautyApp(
    context: ComponentActivity,
    userName: String,
    onLogout: () -> Unit,
    productViewModel: MainViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val productState by productViewModel.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                        // Profile Tab - Show user info and liked products
                        ProfileScreen(
                            userName = userName,
                            likedProducts = productState.products.filter {
                                productState.likedProducts.contains(it.id)
                            },
                            likedProductIds = productState.likedProducts,
                            onToggleLike = { productViewModel.toggleLike(it) },
                            onAddToCart = { productViewModel.addToCart(it) },
                            onProductClick = { product -> selectedProduct = product },
                            onLogout = { showLogoutDialog = true }
                        )
                    }
                }
            }
        }
    }
}