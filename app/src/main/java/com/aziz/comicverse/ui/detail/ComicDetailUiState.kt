package com.aziz.comicverse.ui.detail

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter

data class ComicDetailUiState(
    val comic: Comic? = null,
    val chapters: List<ComicChapter> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
