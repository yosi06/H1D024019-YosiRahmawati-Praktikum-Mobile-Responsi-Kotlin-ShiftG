package com.pemmob.responsikotlinshiftg.data.repository

import com.pemmob.responsikotlinshiftg.data.model.Anime
import com.pemmob.responsikotlinshiftg.data.model.Genre
import com.pemmob.responsikotlinshiftg.data.remote.ApiClient
import com.pemmob.responsikotlinshiftg.data.remote.TenraiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * REPOSITORY LAYER
 * Menjadi satu-satunya sumber data (single source of truth) bagi ViewModel.
 * ViewModel tidak tahu data berasal dari mana (API), cukup memanggil fungsi repository.
 * Hasil dibungkus dalam Result<T> agar sukses/gagal dapat ditangani dengan aman.
 */
class AnimeRepository(
    private val apiService: TenraiApiService = ApiClient.apiService
) {

    /**
     * Mencari anime berdasarkan judul (query) dan genre (opsional).
     * Bila query kosong & tanpa genre -> tampilkan anime terpopuler.
     */
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
                // Collection: hapus duplikat berdasarkan mal_id
                response.data.orEmpty().distinctBy { it.malId }
            }
        }

    /** Mengambil detail anime berdasarkan mal_id */
    suspend fun getAnimeDetail(id: Int): Result<Anime> =
        withContext(Dispatchers.IO) {
            runCatching {
                apiService.getAnimeDetail(id).data
                    ?: throw NoSuchElementException("Data anime tidak ditemukan")
            }
        }

    /** Mengambil daftar genre, diurutkan berdasarkan nama */
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
