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

data class HomeUiState(
    val query: String = "",
    val animeList: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenre: Genre? = null
)

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

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(600L)
            performSearch()
        }
    }

    fun onSearch() {
        searchAnime()
    }

    fun onClearQuery() {
        _uiState.update { it.copy(query = "") }
        searchAnime()
    }

    fun onGenreSelected(genre: Genre?) {
        val current = _uiState.value.selectedGenre
        val newGenre = if (genre?.malId == current?.malId) null else genre
        _uiState.update { it.copy(selectedGenre = newGenre) }
        searchAnime()
    }

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
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(AnimeRepository()) }
        }
    }
}
