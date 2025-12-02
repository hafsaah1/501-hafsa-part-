/*
 * CartItem.kt
 * PURPOSE: Data class for cart items with shade selection support
 * STRUCTURE:
 *   - productId: Int - Product identifier
 *   - quantity: Int - Number of items (default 1)
 *   - selectedShade: ProductColor? - Optional shade selection
 * USAGE: Allows tracking different shades of same product as separate cart items
 */

package com.example.beautyapp.data

data class CartItem(  // ← Parenthesis, not curly brace!
    val productId: Int,
    val quantity: Int = 1,
    val selectedShade: ProductColor? = null
)