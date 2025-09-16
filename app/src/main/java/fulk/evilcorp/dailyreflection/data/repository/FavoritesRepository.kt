package fulk.evilcorp.dailyreflection.data.repository

import fulk.evilcorp.dailyreflection.data.local.FavoriteDao
import fulk.evilcorp.dailyreflection.data.local.FavoriteReflection
import fulk.evilcorp.dailyreflection.model.ReflectionData
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(
    private val dao: FavoriteDao
) {
    fun observeAll(): Flow<List<FavoriteReflection>> = dao.getAll()

    suspend fun add(data: ReflectionData) {
        val dateOnly = extractDateOnly(data.date)
        val id = "${data.source}|$dateOnly"

        val favorite = FavoriteReflection(
            id = id,
            source = data.source,
            title = data.title,
            date = data.date,
            passage = data.passage,
            content = data.content
        )
        dao.insert(favorite)
    }

    suspend fun remove(id: String) = dao.deleteById(id)

    suspend fun isFavorite(id: String): Boolean {
        val result = dao.exists(id) > 0
        return result
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
}