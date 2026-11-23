package com.aziz.comicverse.ui.library

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aziz.comicverse.R
import com.aziz.comicverse.ui.components.ComicCard
import com.aziz.comicverse.ui.components.ComicEmptyState
import com.aziz.comicverse.ui.components.HorizontalComicCard

@Composable
fun LibraryScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReader: (comicId: String, chapterId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Editorial Header with Japanese Sakura Branch Corner
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.ic_sakura_branch_lush),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(118.dp)
                    .alpha(0.82f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sakura_flower),
                        contentDescription = null,
                        tint = Color(0xFF8B1E32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Koleksi favorit dan riwayat membaca tersimpan di perangkat Anda",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tabs (Favorit & Riwayat)
        PrimaryTabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = uiState.selectedTab == LibraryTab.FAVORITES,
                onClick = { viewModel.selectTab(LibraryTab.FAVORITES) },
                text = {
                    Text(
                        text = "Favorit (${uiState.favoriteComics.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (uiState.selectedTab == LibraryTab.FAVORITES) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = uiState.selectedTab == LibraryTab.HISTORY,
                onClick = { viewModel.selectTab(LibraryTab.HISTORY) },
                text = {
                    Text(
                        text = "Riwayat Baca (${uiState.readingHistory.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (uiState.selectedTab == LibraryTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (uiState.selectedTab) {
                LibraryTab.FAVORITES -> {
                    if (uiState.favoriteComics.isEmpty()) {
                        ComicEmptyState(
                            title = "Belum Ada Komik Favorit",
                            message = "Tandai komik favorit Anda di halaman detail dengan tombol bookmark agar tersimpan di sini."
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.favoriteComics, key = { it.id }) { comic ->
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
                LibraryTab.HISTORY -> {
                    if (uiState.readingHistory.isEmpty()) {
                        ComicEmptyState(
                            title = "Belum Ada Riwayat Baca",
                            message = "Mulai membaca bab komik mana pun untuk melihat progress baca Anda di sini."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.readingHistory, key = { it.comicId }) { progress ->
                                val comic = uiState.historyComicsMap[progress.comicId]
                                val title = comic?.title ?: "Komik ${progress.comicId}"
                                val cover = comic?.coverUrl ?: ""
                                val progressPercent = if (progress.totalPages > 0) {
                                    (progress.pageIndex * 100) / progress.totalPages
                                } else 0

                                HorizontalComicCard(
                                    title = title,
                                    coverUrl = cover,
                                    subtitle = "${progress.chapterTitle} • Halaman ${progress.pageIndex}/${progress.totalPages}",
                                    progressPercent = progressPercent,
                                    onClick = {
                                        onNavigateToReader(progress.comicId, progress.chapterId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
