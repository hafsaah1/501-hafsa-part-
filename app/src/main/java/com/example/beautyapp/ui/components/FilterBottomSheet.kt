package com.example.beautyapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    brands: List<String>,
    productTypes: List<String>,
    selectedBrands: Set<String>,
    selectedProductTypes: Set<String>,
    onBrandToggle: (String) -> Unit,
    onProductTypeToggle: (String) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface  // FIXED - theme-aware!
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface  // FIXED - theme-aware!
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface  // FIXED - theme-aware!
                    )
                }
            }

            var selectedTabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Brand", "Product Type")

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,  // FIXED - theme-aware!
                contentColor = Color(0xFFF472B6)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index)
                                    Color(0xFFF472B6)
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // FIXED!
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTabIndex) {
                    0 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(brands) { brand ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        onClick = { onBrandToggle(brand) },
                                        indication = rememberRipple(),
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedBrands.contains(brand),
                                    onCheckedChange = { onBrandToggle(brand) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFF472B6),
                                        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // FIXED!
                                    )
                                )
                                Text(
                                    text = brand,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,  // FIXED!
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }

                    1 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(productTypes) { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        onClick = { onProductTypeToggle(type) },
                                        indication = rememberRipple(),
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedProductTypes.contains(type),
                                    onCheckedChange = { onProductTypeToggle(type) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFF472B6),
                                        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // FIXED!
                                    )
                                )
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,  // FIXED!
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Clear All",
                        color = MaterialTheme.colorScheme.onSurface  // FIXED!
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF472B6)
                    )
                ) {
                    Text(text = "Apply", color = Color.White)
                }
            }
        }
    }
}