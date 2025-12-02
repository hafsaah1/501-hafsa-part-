package com.example.beautyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beautyapp.data.Product
import com.example.beautyapp.ui.components.ProductCard
import com.example.beautyapp.ui.components.FilterBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    products: List<Product>,
    likedProducts: Set<Int>,
    onToggleLike: (Int) -> Unit,
    onAddToCart: (Int) -> Unit,
    loading: Boolean,
    brands: List<String> = emptyList(),
    productTypes: List<String> = emptyList(),
    selectedBrands: Set<String> = emptySet(),
    selectedProductTypes: Set<String> = emptySet(),
    onBrandToggle: (String) -> Unit = {},
    onProductTypeToggle: (String) -> Unit = {},
    onClearFilters: () -> Unit = {},
    hasActiveFilters: Boolean = false,
    onProductClick: (Product) -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Products",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,  // Theme-aware!
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    // Filter Icon with badge
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge(
                                    containerColor = Color(0xFFF472B6)  // Brand color
                                ) {
                                    val count = selectedBrands.size + selectedProductTypes.size
                                    Text(text = count.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurface  // Theme-aware!
                            )
                        }
                    }

                    // Profile Image
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop",
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface  // Theme-aware!
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color(0xFFF472B6),  // Brand color
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (products.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No products found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)  // Theme-aware!
                    )
                    if (hasActiveFilters) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onClearFilters) {
                            Text(
                                text = "Clear filters",
                                color = Color(0xFFF472B6)  // Brand color
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(products) { index, product ->
                        ProductCard(
                            product = product,
                            isLiked = likedProducts.contains(product.id),
                            onToggleLike = onToggleLike,
                            colorIndex = index,
                            onAddToCart = onAddToCart,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            brands = brands,
            productTypes = productTypes,
            selectedBrands = selectedBrands,
            selectedProductTypes = selectedProductTypes,
            onBrandToggle = onBrandToggle,
            onProductTypeToggle = onProductTypeToggle,
            onClearFilters = {
                onClearFilters()
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}