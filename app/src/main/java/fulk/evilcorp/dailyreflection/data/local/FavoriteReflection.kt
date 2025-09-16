package fulk.evilcorp.dailyreflection.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_reflections",
    indices = [Index(value = ["id"], unique = true)]
)
data class FavoriteReflection(
    @PrimaryKey val id: String,
    val source: String,
    val title: String,
    val date: String,
    val passage: String,
    val content: String,
    val dateAdded: Long = System.currentTimeMillis()
)