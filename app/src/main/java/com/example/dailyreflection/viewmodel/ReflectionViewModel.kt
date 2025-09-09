package com.example.dailyreflection.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyreflection.data.ReflectionApi
import com.example.dailyreflection.model.ReflectionData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReflectionViewModel : ViewModel() {
    var reflection = mutableStateOf<ReflectionData?>(null)
        private set

    var isLoading = mutableStateOf(true)
        private set

    var error = mutableStateOf<String?>(null)
        private set

    var selectedSource = mutableStateOf("sh")
        private set

    private val api = ReflectionApi.create()

    init {
        fetchReflection()
    }

    fun selectSource(newSource: String) {
        if (selectedSource.value != newSource) {
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
                reflection.value = fetchReflectionWithRetry(selectedSource.value)
                error.value = null
            } catch (_: Exception) {
                error.value = "Gagal memuat renungan harian"
            } finally {
                isLoading.value = false
            }
        }
    }
}