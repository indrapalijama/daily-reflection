package com.example.dailyreflection.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyreflection.data.ReflectionApi
import com.example.dailyreflection.data.repository.FavoritesRepository
import com.example.dailyreflection.model.ReflectionData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReflectionViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    var reflection = mutableStateOf<ReflectionData?>(null)
        private set

    var isLoading = mutableStateOf(true)
        private set

    var error = mutableStateOf<String?>(null)
        private set

    var selectedSource = mutableStateOf("sh")
        private set

    // Favorites state
    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> = _isFavorite

    private val api = ReflectionApi.create()

    init {
        fetchReflection()
        observeFavoriteChanges()
    }

    // Helper function to extract date only (same as repository)
    private fun extractDateOnly(dateTimeString: String): String {
        return try {
            if (dateTimeString.length >= 10 && dateTimeString[4] == '-' && dateTimeString[7] == '-') {
                dateTimeString.substring(0, 10)
            } else {
                dateTimeString.take(10)
            }
        } catch (_: Exception) {
            dateTimeString.take(10)
        }
    }

    // Observe database changes and sync star state
    private fun observeFavoriteChanges() {
        viewModelScope.launch {
            favoritesRepository.observeAll().collect { favoritesList ->
                val currentReflection = reflection.value
                if (currentReflection != null) {
                    val dateOnly = extractDateOnly(currentReflection.date)
                    val id = "${currentReflection.source}|$dateOnly"
                    val shouldBeFavorite = favoritesList.any { it.id == id }

                    // DEBUG LOGS
                    println("DEBUG: Observer triggered")
                    println("DEBUG: Current reflection - Source: ${currentReflection.source}, Date: ${currentReflection.date}")
                    println("DEBUG: Looking for ID: $id")
                    println("DEBUG: All favorite IDs: ${favoritesList.map { it.id }}")
                    println("DEBUG: Should be favorite: $shouldBeFavorite")
                    println("DEBUG: Current star state: ${_isFavorite.value}")

                    if (_isFavorite.value != shouldBeFavorite) {
                        println("DEBUG: Updating star state from ${_isFavorite.value} to $shouldBeFavorite")
                        _isFavorite.value = shouldBeFavorite
                    }
                }
            }
        }
    }

    fun selectSource(newSource: String) {
        if (selectedSource.value != newSource) {
            println("DEBUG: Changing source from ${selectedSource.value} to $newSource")
            selectedSource.value = newSource
            fetchReflection()
        }
    }

    suspend fun fetchReflectionWithRetry(version: String, maxRetries: Int = 3): ReflectionData {
        var currentAttempt = 0
        var lastError: Exception? = null

        while (currentAttempt < maxRetries) {
            try {
                return api.getReflection(version)
            } catch (e: Exception) {
                lastError = e
                currentAttempt++
                delay(2000L)
            }
        }
        throw lastError ?: Exception("Terjadi kesalahan saat memuat data")
    }

    fun fetchReflection() {
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            try {
                val reflectionData = fetchReflectionWithRetry(selectedSource.value)
                println("DEBUG: Fetched new reflection - Source: ${reflectionData.source}, Date: ${reflectionData.date}")

                // Update reflection first
                reflection.value = reflectionData
                error.value = null

                // Force a delay to ensure observer processes the new reflection
                delay(100)

                // Manual backup check using correct ID format
                val dateOnly = extractDateOnly(reflectionData.date)
                val id = "${reflectionData.source}|$dateOnly"
                val isFav = favoritesRepository.isFavorite(id)
                println("DEBUG: Manual check after fetch - ID: $id, Is favorite: $isFav")
                _isFavorite.value = isFav

            } catch (_: Exception) {
                error.value = "Gagal memuat data, periksa koneksi anda atau coba lagi"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleFavorite(current: ReflectionData?) {
        viewModelScope.launch {
            current ?: return@launch

            val dateOnly = extractDateOnly(current.date)
            val id = "${current.source}|$dateOnly"

            println("DEBUG: toggleFavorite - ID: $id (original: ${current.source}|${current.date})")

            if (favoritesRepository.isFavorite(id)) {
                println("DEBUG: Removing from favorites")
                favoritesRepository.remove(id)
            } else {
                println("DEBUG: Adding to favorites")
                favoritesRepository.add(current)
            }

            // The observer should handle the state change automatically
        }
    }

    fun refreshCurrentFavoriteStatus() {
        viewModelScope.launch {
            reflection.value?.let { data ->
                val dateOnly = extractDateOnly(data.date)
                val id = "${data.source}|$dateOnly"
                _isFavorite.value = favoritesRepository.isFavorite(id)
                println("DEBUG: Manual refresh - ID: $id, Is favorite: ${_isFavorite.value}")
            }
        }
    }
}