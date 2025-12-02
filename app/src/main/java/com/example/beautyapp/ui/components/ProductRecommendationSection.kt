package com.example.beautyapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beautyapp.data.MakeupProduct

@Composable
fun ProductRecommendationSection(products: List<MakeupProduct>) {
    if (products.isNotEmpty()) {
        Text(
            text = "Recommended Products",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Column {
            products.forEach { product ->
                ShadeProductCard(product = product)
            }
        }
    }
}