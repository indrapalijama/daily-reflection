package com.example.dailyreflection

import android.app.Application
import com.example.dailyreflection.data.local.AppDatabase
import com.example.dailyreflection.data.repository.FavoritesRepository

class DailyReflectionApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val favoritesRepository by lazy { FavoritesRepository(database.favoriteDao()) }
}