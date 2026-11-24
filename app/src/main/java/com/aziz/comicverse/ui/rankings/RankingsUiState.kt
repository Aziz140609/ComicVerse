package com.aziz.comicverse.ui.rankings

import com.aziz.comicverse.domain.model.Comic

data class RankingsUiState(
    val selectedTab: Int = 0, // 0 = All Time, 1 = Weekly
    val comics: List<Comic> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
