package com.aziz.comicverse.ui.home

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aziz.comicverse.R
import com.aziz.comicverse.ui.components.ComicErrorState
import com.aziz.comicverse.ui.components.ComicLoadingIndicator
import com.aziz.comicverse.ui.components.CompletedMangaCard
import com.aziz.comicverse.ui.components.HotMangaCard
import com.aziz.comicverse.ui.components.NewUpdateComicCard
import com.aziz.comicverse.ui.components.SearchInputField
import com.aziz.comicverse.ui.components.TerakhirDibacaCard

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
            ComicLoadingIndicator(message = "Memuat katalog manga...")
        }
        uiState.errorMessage != null && uiState.newUpdateComics.isEmpty() && uiState.hotComics.isEmpty() -> {
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
                contentPadding = PaddingValues(bottom = 36.dp)
            ) {
                // 1. Editorial Header
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
                            text = stringResource(R.string.home_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 2. Search Bar Trigger
                item {
                    SearchInputField(
                        query = "",
                        onQueryChange = {},
                        placeholderText = stringResource(R.string.search_placeholder),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onNavigateToExplore(null) }
                    )
                }

                // 3. "Terakhir Di baca" (Continue Reading directly under Search Bar)
                if (uiState.continueReading.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        SectionTitleHeader(
                            title = stringResource(R.string.last_read),
                            actionText = stringResource(R.string.see_all),
                            onActionClick = { onNavigateToExplore(null) }
                        )
                    }

                    items(uiState.continueReading.take(2)) { item ->
                        TerakhirDibacaCard(
                            comic = item.comic,
                            progress = item.progress,
                            onContinueClick = { onNavigateToDetail(item.comic.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                // 4. "New Update" Section Header
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    SectionTitleHeader(
                        title = stringResource(R.string.new_update_manga),
                        actionText = stringResource(R.string.view_schedule),
                        onActionClick = { onNavigateToExplore(null) }
                    )
                }

                // New Update 3-column Grid
                val visibleNewUpdates = if (uiState.isNewUpdateExpanded) {
                    uiState.newUpdateComics
                } else {
                    uiState.newUpdateComics.take(6)
                }

                items(visibleNewUpdates.chunked(3)) { rowComics ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowComics.forEach { comic ->
                            NewUpdateComicCard(
                                comic = comic,
                                onClick = { onNavigateToDetail(comic.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty cells if last row has fewer than 3 items
                        repeat(3 - rowComics.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // "Show More" Button for New Update
                if (uiState.newUpdateComics.size > 6) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.toggleNewUpdateExpanded() },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (uiState.isNewUpdateExpanded) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = if (uiState.isNewUpdateExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. "Hot Manga" Section (Horizontal Scroll with Ranks #1, #2, #3...)
                if (uiState.hotComics.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        SectionTitleHeader(
                            title = stringResource(R.string.hot_manga),
                            actionText = stringResource(R.string.view_rankings),
                            onActionClick = { onNavigateToExplore(null) }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(uiState.hotComics) { index, comic ->
                                HotMangaCard(
                                    comic = comic,
                                    rank = index + 1,
                                    onClick = { onNavigateToDetail(comic.id) }
                                )
                            }
                        }
                    }
                }

                // 6. "Completed Manga" Section (Manga yang sudah selesai)
                if (uiState.completedComics.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitleHeader(
                            title = stringResource(R.string.completed_manga),
                            actionText = stringResource(R.string.see_all),
                            onActionClick = { onNavigateToExplore(null) }
                        )
                    }

                    items(uiState.completedComics.chunked(3)) { rowComics ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowComics.forEach { comic ->
                                CompletedMangaCard(
                                    comic = comic,
                                    onClick = { onNavigateToDetail(comic.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - rowComics.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean Section Header matching user's anime/manga app design
 */
@Composable
private fun SectionTitleHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
