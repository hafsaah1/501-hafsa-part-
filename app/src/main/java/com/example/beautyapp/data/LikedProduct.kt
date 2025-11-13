package com.example.beautyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This tells Room to create a table named "liked_products"
@Entity(tableName = "liked_products")
data class LikedProduct(
    // This is the product ID, e.g., 10, 42, 150
    @PrimaryKey val id: Int
)