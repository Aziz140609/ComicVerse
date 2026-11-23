package com.aziz.comicverse.ui.explore

import com.aziz.comicverse.domain.model.Comic

data class ExploreUiState(
    val query: String = "",
    val selectedGenre: String? = null,
    val comics: List<Comic> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
