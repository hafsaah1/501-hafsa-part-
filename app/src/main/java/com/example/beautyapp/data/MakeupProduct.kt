package com.example.beautyapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ⭐ ADD THIS IMPORT! ⭐
import com.example.beautyapp.data.Shade

// This entity represents products recommended for specific shades (local database)
// This is DIFFERENT from Product.kt which comes from the Makeup API
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Shade::class,
            parentColumns = ["shade_id"],
            childColumns = ["shade_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class MakeupProduct(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: Int,

    @ColumnInfo(name = "shade_id")
    val shadeId: Int,

    val type: String,
    val brand: String,
    val name: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    val price: Double,
    val description: String?
)