package com.pemmob.responsikotlinshiftg.data.remote

import com.pemmob.responsikotlinshiftg.data.model.AnimeDetailResponse
import com.pemmob.responsikotlinshiftg.data.model.AnimeListResponse
import com.pemmob.responsikotlinshiftg.data.model.GenreListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TenraiApiService {

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = null,
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = 25
    ): AnimeListResponse

    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") id: Int
    ): AnimeDetailResponse

    @GET("genres/anime")
    suspend fun getAnimeGenres(): GenreListResponse
}
