package fulk.evilcorp.dailyreflection.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fulk.evilcorp.dailyreflection.data.ReflectionApi
import fulk.evilcorp.dailyreflection.data.repository.FavoritesRepository
import fulk.evilcorp.dailyreflection.model.ReflectionData
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

    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> = _isFavorite

    private val api = ReflectionApi.create()

    init {
        fetchReflection()
        observeFavoriteChanges()
    }

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

    private fun observeFavoriteChanges() {
        viewModelScope.launch {
            favoritesRepository.observeAll().collect { favoritesList ->
                val currentReflection = reflection.value
                if (currentReflection != null) {
                    val dateOnly = extractDateOnly(currentReflection.date)
                    val id = "${currentReflection.source}|$dateOnly"
                    val shouldBeFavorite = favoritesList.any { it.id == id }

                    if (_isFavorite.value != shouldBeFavorite) {
                        _isFavorite.value = shouldBeFavorite
                    }
                }
            }
        }
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
                val reflectionData = fetchReflectionWithRetry(selectedSource.value)

                reflection.value = reflectionData
                error.value = null

                delay(100)

                val dateOnly = extractDateOnly(reflectionData.date)
                val id = "${reflectionData.source}|$dateOnly"
                val isFav = favoritesRepository.isFavorite(id)
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

            if (favoritesRepository.isFavorite(id)) {
                favoritesRepository.remove(id)
            } else {
                favoritesRepository.add(current)
            }
        }
    }

    fun refreshCurrentFavoriteStatus() {
        viewModelScope.launch {
            reflection.value?.let { data ->
                val dateOnly = extractDateOnly(data.date)
                val id = "${data.source}|$dateOnly"
                _isFavorite.value = favoritesRepository.isFavorite(id)
            }
        }
    }
}