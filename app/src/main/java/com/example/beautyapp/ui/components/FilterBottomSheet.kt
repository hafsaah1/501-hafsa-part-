package com.example.beautyapp.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header (Stays the same)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            //  ************************************************
            //  START OF NEW TAB CODE
            //  ************************************************

            // 1. Add state to remember which tab is selected
            var selectedTabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Brand", "Product Type")

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Add the TabRow
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFFF472B6) // Your app's pink color
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            // 3. This Box will hold the correct LazyColumn
            Box(
                modifier = Modifier
                    .weight(1f) // This makes it fill the available space
                    .fillMaxWidth()
            ) {
                // 4. Use `when` to show the correct list
                when (selectedTabIndex) {

                    // --- TAB 0: BRANDS ---
                    0 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp) // Add padding
                    ) {
                        items(brands) { brand ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedBrands.contains(brand),
                                    onCheckedChange = { onBrandToggle(brand) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFF472B6)
                                    )
                                )
                                Text(
                                    text = brand,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }

                    // --- TAB 1: PRODUCT TYPES ---
                    1 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp) // Add padding
                    ) {
                        items(productTypes) { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedProductTypes.contains(type),
                                    onCheckedChange = { onProductTypeToggle(type) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFF472B6)
                                    )
                                )
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    fontSize = 16.sp,
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
            //  ************************************************
            //  END OF NEW TAB CODE
            //  ************************************************


            // Action Buttons (Stays the same)
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
                    Text(text = "Clear All")
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