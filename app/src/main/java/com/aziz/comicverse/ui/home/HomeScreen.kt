package com.aziz.comicverse.ui.home

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    onNavigateToRankings: () -> Unit = {},
    onNavigateToReadingHistory: () -> Unit = {},
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
                // 1. Japanese Atmosphere Header with Mount Fuji & Sakura Silhouette
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                    ) {
                        // Mount Fuji, Sun & Sakura Branch Vector Illustration
                        Image(
                            painter = painterResource(R.drawable.ic_fuji_sakura_header),
                            contentDescription = "Mount Fuji and Sakura Blossom Silhouette",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Smooth transition scrim gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                                            MaterialTheme.colorScheme.background
                                        ),
                                        startY = 50f
                                    )
                                )
                        )

                        // Title, Japanese stamp and Kanji subtitle
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Red Japanese Seal / Hanko Badge (漫画)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFC62828))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "漫画",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ComicVerse",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "コミック",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD81B60).copy(alpha = 0.9f)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_sakura_flower),
                                    contentDescription = null,
                                    tint = Color(0xFFFF80AB),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.home_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                            onActionClick = onNavigateToReadingHistory
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

                // 4. "New Update" Section Header (Schedule removed)
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    SectionTitleHeader(
                        title = stringResource(R.string.new_update_manga)
                    )
                }

                // New Update 3x3 Grid (9 items by default, expands to all)
                val visibleNewUpdates = if (uiState.isNewUpdateExpanded) {
                    uiState.newUpdateComics
                } else {
                    uiState.newUpdateComics.take(9)
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

                // "Show More" Button for New Update (triggers if more than 9 items)
                if (uiState.newUpdateComics.size > 9) {
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
                            onActionClick = onNavigateToRankings
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

                    val visibleCompleted = if (uiState.isCompletedExpanded) {
                        uiState.completedComics
                    } else {
                        uiState.completedComics.take(6)
                    }

                    items(visibleCompleted.chunked(3)) { rowComics ->
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

                    // "Show More" Button for Completed Manga
                    if (uiState.completedComics.size > 6) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleCompletedExpanded() },
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
                                            text = if (uiState.isCompletedExpanded) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = if (uiState.isCompletedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_sakura_flower),
                contentDescription = null,
                tint = Color(0xFFFF80AB),
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFCE4EC))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 11.dp, vertical = 5.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC2185B)
                )
            }
        }
    }
}
