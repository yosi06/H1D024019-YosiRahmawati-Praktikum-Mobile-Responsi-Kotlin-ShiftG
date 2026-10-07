package com.pemmob.responsikotlinshiftg.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pemmob.responsikotlinshiftg.data.model.Anime
import com.pemmob.responsikotlinshiftg.data.repository.AnimeRepository
import com.pemmob.responsikotlinshiftg.ui.navigation.Screen
import com.pemmob.responsikotlinshiftg.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State untuk Detail Screen.
 */
data class DetailUiState(
    val isLoading: Boolean = false,   // State: loading
    val anime: Anime? = null,         // State: data detail
    val errorMessage: String? = null  // State: error
)

/**
 * VIEWMODEL LAYER - DetailViewModel
 * Mengambil mal_id dari argumen navigasi (SavedStateHandle), lalu memuat detail anime
 * dari Repository menggunakan endpoint GET /anime/{id}.
 */
class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AnimeRepository
) : ViewModel() {

    private val animeId: Int = checkNotNull(savedStateHandle[Screen.Detail.ARG_ANIME_ID])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getAnimeDetail(animeId)
                .onSuccess { anime ->
                    _uiState.update { it.copy(isLoading = false, anime = anime) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.toUserMessage())
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DetailViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = AnimeRepository()
                )
            }
        }
    }
}
