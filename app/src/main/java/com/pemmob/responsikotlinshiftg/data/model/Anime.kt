package com.pemmob.responsikotlinshiftg.data.model

import com.google.gson.annotations.SerializedName

data class AnimeListResponse(
    @SerializedName("pagination") val pagination: Pagination? = null,
    @SerializedName("data") val data: List<Anime>? = null
)

data class AnimeDetailResponse(
    @SerializedName("data") val data: Anime? = null
)

data class GenreListResponse(
    @SerializedName("data") val data: List<Genre>? = null
)

data class Pagination(
    @SerializedName("last_visible_page") val lastVisiblePage: Int? = null,
    @SerializedName("has_next_page") val hasNextPage: Boolean? = null,
    @SerializedName("current_page") val currentPage: Int? = null
)

data class Anime(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("title") val title: String? = null,
    @SerializedName("title_english") val titleEnglish: String? = null,
    @SerializedName("title_japanese") val titleJapanese: String? = null,
    @SerializedName("images") val images: Images? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("episodes") val episodes: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("score") val score: Double? = null,
    @SerializedName("scored_by") val scoredBy: Int? = null,
    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("popularity") val popularity: Int? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("season") val season: String? = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("genres") val genres: List<Genre>? = null,
    @SerializedName("studios") val studios: List<Studio>? = null
) {
    val imageUrl: String?
        get() = images?.jpg?.largeImageUrl ?: images?.jpg?.imageUrl ?: images?.webp?.imageUrl

    val scoreText: String
        get() = score?.let { String.format(java.util.Locale.US, "%.2f", it) } ?: "N/A"

    val genreNames: String
        get() = genres.orEmpty().mapNotNull { it.name }.joinToString(", ").ifEmpty { "-" }
}

data class Images(
    @SerializedName("jpg") val jpg: ImageUrl? = null,
    @SerializedName("webp") val webp: ImageUrl? = null
)

data class ImageUrl(
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("small_image_url") val smallImageUrl: String? = null,
    @SerializedName("large_image_url") val largeImageUrl: String? = null
)

data class Genre(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("name") val name: String? = null,
    @SerializedName("count") val count: Int? = null
)

data class Studio(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("name") val name: String? = null
)
