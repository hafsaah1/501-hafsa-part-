package com.example.beautyapp

import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.beautyapp.data.ProductColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.Settings
import com.example.beautyapp.ui.screens.*
import com.example.beautyapp.ui.components.BottomNavBar
import com.example.beautyapp.ui.theme.BeautyAppTheme
import com.example.beautyapp.viewmodel.MainViewModel
import com.example.beautyapp.viewmodel.WeatherViewModel
import com.example.beautyapp.viewmodel.SettingsViewModel
import com.example.beautyapp.viewmodel.ShadeProductViewModel  // ← NEW IMPORT
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // CRITICAL: Collect settings HERE in composable scope
            // This allows the theme to update when settings change
            val settings: Settings by settingsViewModel.settings.collectAsState()

            // Apply theme based on user's dark mode setting
            // When user toggles dark mode, settings changes, this recomposes, theme switches!
            BeautyAppTheme(darkTheme = settings.isDarkMode) {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val isLoggedIn = currentUser != null

                Log.d("MainActivity", "onCreate - isLoggedIn: $isLoggedIn, user: ${currentUser?.displayName}")

                if (isLoggedIn) {
                    BeautyApp(
                        context = this@MainActivity,
                        userName = currentUser?.displayName ?: "User",
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            recreate()
                        },
                        settings = settings  // Pass settings to app --- BRAAAAAA
                    )
                } else {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userName ->
                    Log.d("AppNavigation", "Login success: $userName")
                    navController.navigate("main/$userName") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("signup")
                }
            )
        }

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

        composable("main/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            Log.d("AppNavigation", "Showing main for: $userName")

            val context = LocalContext.current as ComponentActivity
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            BeautyApp(
                context = context,
                userName = userName,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                settings = settings
            )
        }
    }
}

@Composable
fun BeautyApp(
    context: ComponentActivity,
    userName: String,
    onLogout: () -> Unit,
    settings: Settings,  // Receive settings
    productViewModel: MainViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    shadeProductViewModel: ShadeProductViewModel = viewModel()  // ← NEW VIEWMODEL
) {
    val productState by productViewModel.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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

    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct!!,
            isLiked = productState.likedProducts.contains(selectedProduct!!.id),
            onToggleLike = { productViewModel.toggleLike(it) },
            onAddToCart = { productId: Int, shade: ProductColor? ->
                productViewModel.addToCart(productId, shade)
            },
            onBack = { selectedProduct = null }
        )
    } else {
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
                    0 -> WeatherScreen(
                        modifier = Modifier.fillMaxSize(),
                        context = context,
                        viewModel = weatherViewModel,
                        userName = userName
                    )
                    1 -> ProductsScreen(
                        products = productViewModel.getDisplayProducts(),
                        likedProducts = productState.likedProducts,
                        onToggleLike = { productViewModel.toggleLike(it) },
                        onAddToCart = { productViewModel.addToCart(it) },  // OK - ProductCard doesn't select shades
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

                    // ========================================
                    // UPDATED: Shade Matching Screen!
                    // ========================================
                    2 -> ShadeProductScreen(viewModel = shadeProductViewModel)

                    3 -> CartScreen(
                        cartItems = productState.cartItems,  // Now List<CartItem>!
                        products = productState.products,
                        onAddToCart = { productId, shade -> productViewModel.addToCart(productId, shade) },  // FIXED - added shade!
                        onRemoveFromCart = { productId, shade -> productViewModel.removeFromCart(productId, shade) }  // FIXED - added shade!
                    )

                    4 -> ProfileScreen(
                        userName = userName,
                        likedProducts = productState.products.filter {
                            productState.likedProducts.contains(it.id)
                        },
                        likedProductIds = productState.likedProducts,
                        onToggleLike = { productViewModel.toggleLike(it) },
                        onAddToCart = { productViewModel.addToCart(it) },  // OK - Profile favorites don't select shades
                        onProductClick = { product -> selectedProduct = product },
                        onLogout = { showLogoutDialog = true },
                        viewModel = productViewModel
                    )
                }
            }
        }
    }
}