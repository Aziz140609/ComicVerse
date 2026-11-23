package com.aziz.comicverse.ui.home

import com.aziz.comicverse.domain.model.Comic

data class HomeUiState(
    val featuredComics: List<Comic> = emptyList(),
    val popularComics: List<Comic> = emptyList(),
    val continueReading: List<Comic> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
