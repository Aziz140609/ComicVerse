package com.aziz.comicverse.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.ui.components.ComicCard
import com.aziz.comicverse.ui.components.ComicErrorState
import com.aziz.comicverse.ui.components.ComicLoadingIndicator
import com.aziz.comicverse.ui.components.ComicSectionHeader
import com.aziz.comicverse.ui.components.HorizontalComicCard
import com.aziz.comicverse.ui.components.SearchInputField
import com.aziz.comicverse.ui.components.StatusBadge

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToExplore: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            ComicLoadingIndicator(message = "Memuat rekomendasi komik...")
        }
        uiState.errorMessage != null && uiState.popularComics.isEmpty() -> {
            ComicErrorState(
                message = uiState.errorMessage ?: "Terjadi kesalahan.",
                onRetry = { viewModel.loadHomeData() }
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Editorial Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ComicVerse",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "コミック",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Temukan komik favorit dan baca secara vertikal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Search Bar Trigger
                item {
                    SearchInputField(
                        query = "",
                        onQueryChange = {},
                        placeholderText = "Cari komik, manga, atau author...",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onNavigateToExplore(null) }
                    )
                }

                // Featured Banner (Editorial Highlight)
                if (uiState.featuredComics.isNotEmpty()) {
                    item {
                        ComicSectionHeader(
                            title = "Pilihan Editor",
                            subtitle = "Karya pilihan minggu ini"
                        )
                        FeaturedCarousel(
                            comics = uiState.featuredComics,
                            onComicClick = onNavigateToDetail
                        )
                    }
                }

                // Continue Reading (if any demo/history available)
                if (uiState.continueReading.isNotEmpty()) {
                    item {
                        ComicSectionHeader(
                            title = "Lanjutkan Membaca",
                            subtitle = "Mulai dari halaman terakhir",
                            actionLabel = "Semua",
                            onActionClick = { onNavigateToExplore(null) }
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.continueReading.take(2).forEach { comic ->
                                HorizontalComicCard(
                                    title = comic.title,
                                    coverUrl = comic.coverUrl,
                                    subtitle = "${comic.author} • ${comic.status}",
                                    progressPercent = 45,
                                    onClick = { onNavigateToDetail(comic.id) }
                                )
                            }
                        }
                    }
                }

                // Popular Comics Row
                if (uiState.popularComics.isNotEmpty()) {
                    item {
                        ComicSectionHeader(
                            title = "Komik Populer",
                            subtitle = "Paling banyak diikuti pembaca",
                            actionLabel = "Lihat Semua",
                            onActionClick = { onNavigateToExplore(null) }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.popularComics) { comic ->
                                ComicCard(
                                    title = comic.title,
                                    coverUrl = comic.coverUrl,
                                    author = comic.author,
                                    genre = comic.genres.firstOrNull(),
                                    rating = comic.rating,
                                    modifier = Modifier.width(140.dp),
                                    onClick = { onNavigateToDetail(comic.id) }
                                )
                            }
                        }
                    }
                }

                // Quick Genre Discovery
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ComicSectionHeader(
                        title = "Jelajahi Genre",
                        subtitle = "Koleksi berdasarkan kategori"
                    )
                    val genres = listOf("Action", "Fantasy", "Comedy", "Sci-Fi", "Historical", "Drama")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(genres) { genre ->
                            Card(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { onNavigateToExplore(genre) },
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = genre,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedCarousel(
    comics: List<Comic>,
    onComicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(comics) { comic ->
            Card(
                modifier = Modifier
                    .width(280.dp)
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { onComicClick(comic.id) },
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(comic.coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = comic.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient Overlay for text contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    ),
                                    startY = 60f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        StatusBadge(status = comic.status)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = comic.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = comic.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
