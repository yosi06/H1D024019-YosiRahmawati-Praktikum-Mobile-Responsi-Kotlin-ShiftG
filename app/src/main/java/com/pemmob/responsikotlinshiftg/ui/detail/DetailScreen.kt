package com.pemmob.responsikotlinshiftg.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pemmob.responsikotlinshiftg.data.model.Anime
import com.pemmob.responsikotlinshiftg.ui.components.ErrorView
import com.pemmob.responsikotlinshiftg.ui.components.LoadingView
import com.pemmob.responsikotlinshiftg.ui.theme.SunsetGold

/**
 * DETAIL SCREEN (stateful)
 * Menampilkan: Judul, Genre, Rating, Jumlah episode, Status, dan Sinopsis.
 */
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadDetail
    )
}

/** DETAIL CONTENT (stateless) */
@Composable
fun DetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingView(
                    modifier = Modifier.padding(innerPadding),
                    message = "Memuat detail anime..."
                )
                uiState.errorMessage != null -> ErrorView(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding)
                )
                uiState.anime != null -> AnimeDetailBody(
                    anime = uiState.anime,
                    bottomPadding = innerPadding.calculateBottomPadding()
                )
            }

            // Tombol kembali
            FilledIconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding() + 8.dp, start = 12.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimeDetailBody(anime: Anime, bottomPadding: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding + 24.dp)
    ) {
        // ===== Header: banner blur + poster =====
        Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.35f),
                            0.6f to MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                            1f to MaterialTheme.colorScheme.background
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 20.dp)
                    .offset(y = 20.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                AsyncImage(
                    model = anime.imageUrl,
                    contentDescription = anime.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(130.dp)
                        .height(190.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    // Judul anime
                    Text(
                        text = anime.title ?: "Tanpa Judul",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    anime.titleEnglish?.takeIf { it != anime.title }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    anime.titleJapanese?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        // ===== Info: Rating, Episode, Status =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoCard(
                icon = Icons.Filled.Star,
                iconTint = SunsetGold,
                label = "Rating",
                value = anime.scoreText,
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = Icons.Filled.PlayArrow,
                iconTint = MaterialTheme.colorScheme.primary,
                label = "Episode",
                value = anime.episodes?.toString() ?: "?",
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = Icons.Filled.CheckCircle,
                iconTint = MaterialTheme.colorScheme.secondary,
                label = "Status",
                value = anime.status ?: "-",
                modifier = Modifier.weight(1.4f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ===== Genre =====
        SectionTitle("Genre")
        FlowRow(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val genres = anime.genres.orEmpty()
            if (genres.isEmpty()) {
                Text("-", style = MaterialTheme.typography.bodyMedium)
            }
            genres.forEach { genre ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = genre.name.orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ===== Informasi tambahan =====
        SectionTitle("Informasi")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRow("Tipe", anime.type)
                InfoRow("Sumber", anime.source)
                InfoRow("Durasi", anime.duration)
                InfoRow("Musim", listOfNotNull(anime.season?.replaceFirstChar { it.uppercase() }, anime.year?.toString())
                    .joinToString(" ").ifEmpty { null })
                InfoRow("Studio", anime.studios.orEmpty().mapNotNull { it.name }.joinToString(", ").ifEmpty { null })
                InfoRow("Peringkat", anime.rank?.let { "#$it" })
                InfoRow("Klasifikasi", anime.rating)
            }
        }

        Spacer(Modifier.height(20.dp))

        // ===== Sinopsis =====
        SectionTitle("Sinopsis")
        Text(
            text = anime.synopsis?.takeIf { it.isNotBlank() } ?: "Sinopsis belum tersedia.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
