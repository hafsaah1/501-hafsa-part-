package com.example.beautyapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedProductDao {

    // Insert a new liked product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun likeProduct(likedProduct: LikedProduct)

    // Delete a liked product
    @Delete
    suspend fun unlikeProduct(likedProduct: LikedProduct)

    // Get all liked product IDs as a Flow (so it updates automatically)
    @Query("SELECT id FROM liked_products")
    fun getAllLikedProductIds(): Flow<List<Int>>
}