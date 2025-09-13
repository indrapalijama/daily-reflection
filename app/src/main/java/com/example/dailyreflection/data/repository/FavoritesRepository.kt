package com.example.dailyreflection.data.repository

import com.example.dailyreflection.data.local.FavoriteDao
import com.example.dailyreflection.data.local.FavoriteReflection
import com.example.dailyreflection.model.ReflectionData
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(
    private val dao: FavoriteDao
) {
    fun observeAll(): Flow<List<FavoriteReflection>> = dao.getAll()

    suspend fun add(data: ReflectionData) {
        // Simple approach: just take the first 10 characters (YYYY-MM-DD)
        val dateOnly = extractDateOnly(data.date)
        val id = "${data.source}|$dateOnly"

        println("DEBUG: Adding to favorites with ID: $id (original date: ${data.date})")

        val favorite = FavoriteReflection(
            id = id,
            source = data.source,
            title = data.title,
            date = data.date, // Keep original date for display
            passage = data.passage,
            content = data.content
        )
        dao.insert(favorite)
    }

    suspend fun remove(id: String) = dao.deleteById(id)

    suspend fun isFavorite(id: String): Boolean {
        val result = dao.exists(id) > 0
        println("DEBUG: Checking if favorite - ID: $id, Result: $result")
        return result
    }

    // Simple date extraction that works on all API levels
    private fun extractDateOnly(dateTimeString: String): String {
        return try {
            // For "2025-09-13T07:54:28.495Z", take first 10 chars: "2025-09-13"
            if (dateTimeString.length >= 10 && dateTimeString[4] == '-' && dateTimeString[7] == '-') {
                dateTimeString.substring(0, 10)
            } else {
                dateTimeString.take(10)
            }
        } catch (_: Exception) {
            dateTimeString.take(10)
        }
    }
}