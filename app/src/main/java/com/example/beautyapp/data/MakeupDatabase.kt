package com.example.beautyapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ⭐ THESE IMPORTS WERE MISSING! ⭐
import com.example.beautyapp.data.Shade
import com.example.beautyapp.data.MakeupProduct

// Separate database for shade matching feature
// This is DIFFERENT from AppDatabase (which has CartItem, LikedProduct, Note)
@Database(
    entities = [Shade::class, MakeupProduct::class],
    version = 1,
    exportSchema = false
)
abstract class MakeupDatabase : RoomDatabase() {

    abstract fun shadeDao(): ShadeDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: MakeupDatabase? = null

        fun getDatabase(context: Context): MakeupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MakeupDatabase::class.java,
                    "makeup_database"
                )
                    .createFromAsset("databases/makeup.db") // Pre-populated database from assets
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}