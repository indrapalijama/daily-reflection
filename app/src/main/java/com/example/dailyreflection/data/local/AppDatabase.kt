package com.example.dailyreflection.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [FavoriteReflection::class],
    version = 2, // Increment version due to schema change
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reflection_database"
                )
                    .fallbackToDestructiveMigration() // Add this to handle schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}