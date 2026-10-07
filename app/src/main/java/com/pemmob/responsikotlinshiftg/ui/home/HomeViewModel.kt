package com.pemmob.responsikotlinshiftg.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pemmob.responsikotlinshiftg.data.model.Anime
import com.pemmob.responsikotlinshiftg.data.model.Genre
import com.pemmob.responsikotlinshiftg.data.repository.AnimeRepository
import com.pemmob.responsikotlinshiftg.util.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State untuk Home Screen.
 * Setiap perubahan nilai pada state ini akan memicu RECOMPOSITION pada UI.
 */
data class HomeUiState(
    val query: String = "",                  // State: query pencarian
    val animeList: List<Anime> = emptyList(), // State: data hasil pencarian
    val isLoading: Boolean = false,          // State: loading
    val errorMessage: String? = null,        // State: error
    val genres: List<Genre> = emptyList(),   // State: daftar genre untuk filter
    val selectedGenre: Genre? = null         // State: genre yang dipilih
)

/**
 * VIEWMODEL LAYER - HomeViewModel
 * Menyimpan & mengelola state Home Screen, memanggil Repository di dalam Coroutine
 * (viewModelScope). Composable hanya mengamati state & mengirim event.
 */
class HomeViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadGenres()
        searchAnime()
    }

    /** Dipanggil setiap kali user mengetik di search bar (dengan debounce 600ms) */
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(600L) // debounce agar tidak spam request ke API
            performSearch()
        }
    }

    /** Dipanggil ketika user menekan tombol search pada keyboard */
    fun onSearch() {
        searchAnime()
    }

    /** Menghapus query pencarian */
    fun onClearQuery() {
        _uiState.update { it.copy(query = "") }
        searchAnime()
    }

    /** Memilih / membatalkan filter genre (null = Semua genre) */
    fun onGenreSelected(genre: Genre?) {
        val current = _uiState.value.selectedGenre
        val newGenre = if (genre?.malId == current?.malId) null else genre
        _uiState.update { it.copy(selectedGenre = newGenre) }
        searchAnime()
    }

    /** Mencoba ulang ketika terjadi error */
    fun retry() {
        if (_uiState.value.genres.isEmpty()) loadGenres()
        searchAnime()
    }

    private fun searchAnime() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch { performSearch() }
    }

    private suspend fun performSearch() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        repository.searchAnime(state.query, state.selectedGenre?.malId)
            .onSuccess { list ->
                _uiState.update { it.copy(isLoading = false, animeList = list) }
            }
            .onFailure { throwable ->
                if (throwable is kotlinx.coroutines.CancellationException) throw throwable
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = throwable.toUserMessage())
                }
            }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            repository.getGenres().onSuccess { genres ->
                _uiState.update { it.copy(genres = genres) }
            }
        }
    }

    companion object {
        /** Factory untuk membuat HomeViewModel beserta dependensinya (Repository) */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(AnimeRepository()) }
        }
    }
}
