package com.aziz.comicverse.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aziz.comicverse.ui.components.ComicCard
import com.aziz.comicverse.ui.components.ComicEmptyState
import com.aziz.comicverse.ui.components.ComicErrorState
import com.aziz.comicverse.ui.components.ComicLoadingIndicator
import com.aziz.comicverse.ui.components.GenreChip
import com.aziz.comicverse.ui.components.SearchInputField

private val AVAILABLE_GENRES = listOf(
    "All",
    "Action",
    "Fantasy",
    "Comedy",
    "Sci-Fi",
    "Historical",
    "Adventure",
    "Drama",
    "Mystery"
)

@Composable
fun ExploreScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialGenre: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: ExploreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialGenre) {
        if (!initialGenre.isNullOrBlank()) {
            viewModel.selectGenre(initialGenre)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with Back Navigation support
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Beranda",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(
                    text = "Explore",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Cari berdasarkan judul, tema, atau genre",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Input
        SearchInputField(
            query = uiState.query,
            onQueryChange = { viewModel.onQueryChange(it) },
            placeholderText = "Cari judul komik...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            onSearch = { /* Handled automatically via query changes */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Genre Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AVAILABLE_GENRES) { genre ->
                val isSelected = if (genre == "All") {
                    uiState.selectedGenre == null
                } else {
                    uiState.selectedGenre == genre
                }

                GenreChip(
                    label = genre,
                    isSelected = isSelected,
                    onClick = {
                        if (genre == "All") {
                            viewModel.selectGenre(null)
                        } else {
                            viewModel.selectGenre(genre)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results Section
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.isLoading -> {
                    ComicLoadingIndicator(message = "Mencari komik...")
                }
                uiState.errorMessage != null && uiState.comics.isEmpty() -> {
                    ComicErrorState(
                        message = uiState.errorMessage ?: "Terjadi kesalahan.",
                        onRetry = { viewModel.loadInitialComics() }
                    )
                }
                uiState.comics.isEmpty() -> {
                    ComicEmptyState(
                        title = "Komik Tidak Ditemukan",
                        message = "Coba kata kunci lain atau pilih genre yang berbeda."
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.comics, key = { it.id }) { comic ->
                            ComicCard(
                                title = comic.title,
                                coverUrl = comic.coverUrl,
                                author = comic.author,
                                genre = comic.genres.firstOrNull(),
                                rating = comic.rating,
                                onClick = { onNavigateToDetail(comic.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
