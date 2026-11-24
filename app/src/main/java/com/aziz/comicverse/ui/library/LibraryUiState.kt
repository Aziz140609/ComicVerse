package com.aziz.comicverse.ui.library

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ReadingProgress

enum class LibraryTab {
    FAVORITES,
    HISTORY
}

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.FAVORITES,
    val favoriteComics: List<Comic> = emptyList(),
    val readingHistory: List<ReadingProgress> = emptyList(),
    val historyComicsMap: Map<String, Comic> = emptyMap(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
