package com.aziz.comicverse.ui.home

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ReadingProgress

data class ContinueReadingItem(
    val comic: Comic,
    val progress: ReadingProgress
)

data class HomeUiState(
    val continueReading: List<ContinueReadingItem> = emptyList(),
    val newUpdateComics: List<Comic> = emptyList(),
    val isNewUpdateExpanded: Boolean = false,
    val hotComics: List<Comic> = emptyList(),
    val completedComics: List<Comic> = emptyList(),
    val isCompletedExpanded: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
