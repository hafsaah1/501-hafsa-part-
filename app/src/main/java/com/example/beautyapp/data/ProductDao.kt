package com.example.beautyapp.data  // ← CORRECTED!

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE shade_id = :shadeId")
    fun getProductsForShade(shadeId: Int): List<MakeupProduct>

    @Query("SELECT * FROM products WHERE product_id = :productId")
    fun getProductById(productId: Int): MakeupProduct?
}