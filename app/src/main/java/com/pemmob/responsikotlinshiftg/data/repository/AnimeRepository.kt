package com.pemmob.responsikotlinshiftg.data.repository

import com.pemmob.responsikotlinshiftg.data.model.Anime
import com.pemmob.responsikotlinshiftg.data.model.Genre
import com.pemmob.responsikotlinshiftg.data.remote.ApiClient
import com.pemmob.responsikotlinshiftg.data.remote.TenraiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeRepository(
    private val apiService: TenraiApiService = ApiClient.apiService
) {

    suspend fun searchAnime(query: String, genreId: Int?): Result<List<Anime>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val trimmed = query.trim()
                val response = if (trimmed.isEmpty()) {
                    apiService.searchAnime(
                        genres = genreId,
                        orderBy = "popularity",
                        sort = "asc"
                    )
                } else {
                    apiService.searchAnime(query = trimmed, genres = genreId)
                }
                response.data.orEmpty().distinctBy { it.malId }
            }
        }

    suspend fun getAnimeDetail(id: Int): Result<Anime> =
        withContext(Dispatchers.IO) {
            runCatching {
                apiService.getAnimeDetail(id).data
                    ?: throw NoSuchElementException("Data anime tidak ditemukan")
            }
        }

    suspend fun getGenres(): Result<List<Genre>> =
        withContext(Dispatchers.IO) {
            runCatching {
                apiService.getAnimeGenres().data.orEmpty()
                    .filter { !it.name.isNullOrBlank() }
                    .distinctBy { it.malId }
                    .sortedBy { it.name }
            }
        }
}
