package fulk.evilcorp.dailyreflection.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fulk.evilcorp.dailyreflection.data.repository.FavoritesRepository
import fulk.evilcorp.dailyreflection.viewmodel.FavoritesViewModel
import fulk.evilcorp.dailyreflection.viewmodel.ReflectionViewModel

class ViewModelFactory(
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ReflectionViewModel::class.java) -> {
                ReflectionViewModel(favoritesRepository) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(favoritesRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}