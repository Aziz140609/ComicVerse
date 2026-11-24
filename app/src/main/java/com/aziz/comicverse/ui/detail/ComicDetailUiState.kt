package com.aziz.comicverse.ui.detail

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ReadingProgress

data class ComicDetailUiState(
    val comic: Comic? = null,
    val chapters: List<ComicChapter> = emptyList(),
    val isFavorite: Boolean = false,
    val readingProgress: ReadingProgress? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
