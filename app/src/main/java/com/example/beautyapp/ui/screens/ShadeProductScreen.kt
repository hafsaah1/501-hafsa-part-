package com.example.beautyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beautyapp.ui.components.ProductRecommendationSection
import com.example.beautyapp.ui.components.ShadeSelectionSection
import com.example.beautyapp.viewmodel.ShadeProductViewModel

@Composable
fun ShadeProductScreen(
    viewModel: ShadeProductViewModel
) {
    val shades by viewModel.shades.observeAsState(emptyList())
    val selectedShade by viewModel.selectedShade.observeAsState()
    val products by viewModel.products.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Shade selection grid
        ShadeSelectionSection(
            shades = shades,
            selectedShade = selectedShade,
            onShadeSelected = { viewModel.onShadeSelected(it) }
        )

        Spacer(Modifier.height(32.dp))

        // Display selected shade with color circle
        selectedShade?.let {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Your Shade", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(it.hexCode)))
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = it.description ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Product recommendations
        ProductRecommendationSection(products = products)
    }
}