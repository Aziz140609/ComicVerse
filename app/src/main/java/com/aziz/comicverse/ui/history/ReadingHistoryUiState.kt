package com.aziz.comicverse.ui.history

import com.aziz.comicverse.ui.home.ContinueReadingItem

data class ReadingHistoryUiState(
    val historyItems: List<ContinueReadingItem> = emptyList(),
    val isLoading: Boolean = true
)
