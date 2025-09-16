package fulk.evilcorp.dailyreflection.model

import com.google.gson.annotations.SerializedName

data class ReflectionData(
    @SerializedName("Source")
    val source: String,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("Passage")
    val passage: String,
    @SerializedName("Content")
    val content: String
)