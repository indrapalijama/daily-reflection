package com.example.dailyreflection.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyreflection.data.ReflectionApi
import com.example.dailyreflection.model.ReflectionData
import kotlinx.coroutines.launch

class ReflectionViewModel : ViewModel() {
    var reflection = mutableStateOf<ReflectionData?>(null)
        private set
    var isLoading = mutableStateOf(true)
        private set
    var error = mutableStateOf<String?>(null)
        private set

    private val api = ReflectionApi.create()

    init {
        fetchReflection()
    }

    fun fetchReflection() {
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            try {
                reflection.value = api.getReflection()
            } catch (ex: Exception) {
                error.value = "Failed to load reflection."
            } finally {
                isLoading.value = false
            }
        }
    }
}