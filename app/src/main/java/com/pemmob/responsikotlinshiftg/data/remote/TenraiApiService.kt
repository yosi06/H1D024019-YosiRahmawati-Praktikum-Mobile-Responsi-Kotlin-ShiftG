package com.pemmob.responsikotlinshiftg.data.remote

import com.pemmob.responsikotlinshiftg.data.model.AnimeDetailResponse
import com.pemmob.responsikotlinshiftg.data.model.AnimeListResponse
import com.pemmob.responsikotlinshiftg.data.model.GenreListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface Retrofit yang mendefinisikan endpoint Tenrai API.
 * Base URL: https://api.tenrai.org/v1/
 * Semua fungsi adalah suspend function sehingga dipanggil di dalam Coroutine.
 */
interface TenraiApiService {

    /**
     * Pencarian anime berdasarkan judul + filter genre.
     * Contoh: GET https://api.tenrai.org/v1/anime?q=naruto
     *         GET https://api.tenrai.org/v1/anime?q=naruto&genres=1
     */
    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = null,
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = 25
    ): AnimeListResponse

    /**
     * Detail anime berdasarkan mal_id.
     * Contoh: GET https://api.tenrai.org/v1/anime/1
     */
    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") id: Int
    ): AnimeDetailResponse

    /**
     * Daftar genre anime untuk fitur filter.
     * Contoh: GET https://api.tenrai.org/v1/genres/anime
     */
    @GET("genres/anime")
    suspend fun getAnimeGenres(): GenreListResponse
}
