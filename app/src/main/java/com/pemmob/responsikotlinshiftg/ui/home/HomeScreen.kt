package com.pemmob.responsikotlinshiftg.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.responsikotlinshiftg.data.model.Genre
import com.pemmob.responsikotlinshiftg.ui.components.AnimeCard
import com.pemmob.responsikotlinshiftg.ui.components.EmptyView
import com.pemmob.responsikotlinshiftg.ui.components.ErrorView
import com.pemmob.responsikotlinshiftg.ui.components.LoadingView

/**
 * HOME SCREEN (stateful)
 * Mengambil state dari HomeViewModel. Composable TIDAK memanggil API secara langsung.
 */
@Composable
fun HomeScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    // collectAsStateWithLifecycle -> setiap state berubah, Compose melakukan recomposition
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::onSearch,
        onClearQuery = viewModel::onClearQuery,
        onGenreSelected = viewModel::onGenreSelected,
        onRetry = viewModel::retry,
        onAnimeClick = onAnimeClick
    )
}

/**
 * HOME CONTENT (stateless) - hanya menerima state & lambda event.
 */
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit,
    onGenreSelected: (Genre?) -> Unit,
    onRetry: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeHeader()

            AnimeSearchBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onClear = onClearQuery
            )

            GenreFilterRow(
                genres = uiState.genres,
                selectedGenre = uiState.selectedGenre,
                onGenreSelected = onGenreSelected
            )

            // State-driven UI: tampilan ditentukan oleh nilai state
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> LoadingView()
                    uiState.errorMessage != null -> ErrorView(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                    uiState.animeList.isEmpty() -> EmptyView(query = uiState.query)
                    else -> AnimeGrid(uiState = uiState, onAnimeClick = onAnimeClick)
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)) {
        // Judul aplikasi
        Text(
            text = buildAnnotatedString {
                append("Pipit")
                withStyle(
                    SpanStyle(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                ) { append("Nime") }
            },
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Cari anime favoritmu bareng Pipit ✨",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Search bar pencarian anime berdasarkan judul */
@Composable
private fun AnimeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        placeholder = { Text("Cari judul anime, mis. Naruto") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Cari") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Clear, contentDescription = "Hapus")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            onSearch()
        }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
        )
    )
}

/** Filter berdasarkan genre */
@Composable
private fun GenreFilterRow(
    genres: List<Genre>,
    selectedGenre: Genre?,
    onGenreSelected: (Genre?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "all") {
            GenreChip(
                label = "Semua",
                selected = selectedGenre == null,
                onClick = { onGenreSelected(null) }
            )
        }
        items(genres, key = { it.malId }) { genre ->
            GenreChip(
                label = genre.name.orEmpty(),
                selected = genre.malId == selectedGenre?.malId,
                onClick = { onGenreSelected(genre) }
            )
        }
    }
}

@Composable
private fun GenreChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = null
    )
}

/** Daftar anime menggunakan LazyVerticalGrid */
@Composable
private fun AnimeGrid(uiState: HomeUiState, onAnimeClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    text = when {
                        uiState.query.isNotBlank() -> "Hasil pencarian \"${uiState.query}\""
                        uiState.selectedGenre != null -> "Anime ${uiState.selectedGenre.name} terpopuler"
                        else -> "Anime Terpopuler"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${uiState.animeList.size} anime ditemukan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
            }
        }
        items(uiState.animeList, key = { it.malId }) { anime ->
            AnimeCard(anime = anime, onClick = onAnimeClick)
        }
    }
}
