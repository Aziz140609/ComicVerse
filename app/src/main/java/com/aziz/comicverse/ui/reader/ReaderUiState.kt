package com.aziz.comicverse.ui.reader

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ComicPage

data class ReaderUiState(
    val comic: Comic? = null,
    val currentChapter: ComicChapter? = null,
    val pages: List<ComicPage> = emptyList(),
    val prevChapterId: String? = null,
    val nextChapterId: String? = null,
    val currentPageIndex: Int = 1,
    val totalPages: Int = 0,
    val isControlsVisible: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
