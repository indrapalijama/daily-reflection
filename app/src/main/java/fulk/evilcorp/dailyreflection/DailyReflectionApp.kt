package fulk.evilcorp.dailyreflection

import android.app.Application
import fulk.evilcorp.dailyreflection.data.local.AppDatabase
import fulk.evilcorp.dailyreflection.data.repository.FavoritesRepository

class DailyReflectionApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val favoritesRepository by lazy { FavoritesRepository(database.favoriteDao()) }
}