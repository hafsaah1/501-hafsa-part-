package com.example.beautyapp.data  // ← CORRECTED!

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ShadeDao {
    @Query("SELECT * FROM shades")
    fun getAllShades(): List<Shade>

    @Query("SELECT * FROM shades WHERE shade_id = :id")
    fun getShadeById(id: Int): Shade?
}