package com.example.beautyapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// This entity represents skin shades in the local database
@Entity(tableName = "shades")
data class Shade(
    @PrimaryKey
    @ColumnInfo(name = "shade_id")
    val shadeId: Int,

    @ColumnInfo(name = "hex_code")
    val hexCode: String,

    @ColumnInfo(name = "undertone")
    val undertone: String?,

    @ColumnInfo(name = "description")
    val description: String?
)