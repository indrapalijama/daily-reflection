package com.example.dailyreflection.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_reflections",
    indices = [Index(value = ["id"], unique = true)] // Add closing parenthesis here
)
data class FavoriteReflection(
    @PrimaryKey val id: String, // e.g. "sh|2025-09-13"
    val source: String,
    val title: String,
    val date: String,
    val passage: String,
    val content: String,
    val dateAdded: Long = System.currentTimeMillis()
)