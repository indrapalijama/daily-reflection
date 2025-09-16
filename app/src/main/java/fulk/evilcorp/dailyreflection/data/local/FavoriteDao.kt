package fulk.evilcorp.dailyreflection.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_reflections ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<FavoriteReflection>>

    @Query("SELECT * FROM favorite_reflections WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): FavoriteReflection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteReflection)

    @Query("DELETE FROM favorite_reflections WHERE id = :id")
    suspend fun deleteById(id: String)


    @Query("SELECT COUNT(*) FROM favorite_reflections WHERE id = :id")
    suspend fun exists(id: String): Int
}