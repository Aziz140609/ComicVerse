package com.aziz.comicverse.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReaderViewModel(
    val comicId: String,
    val initialChapterId: String,
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadChapter(initialChapterId)
    }

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentPageIndex = 1, errorMessage = null) }

            val comicResult = repository.getComicDetail(comicId)
            val chaptersResult = repository.getComicChapters(comicId)
            val pagesResult = repository.getChapterPages(comicId, chapterId)

            val chapters = chaptersResult.getOrDefault(emptyList())
            val currentIdx = chapters.indexOfFirst { it.id == chapterId }
            val currentChapter = chapters.getOrNull(currentIdx)

            val prevChapterId = if (currentIdx > 0) chapters[currentIdx - 1].id else null
            val nextChapterId = if (currentIdx in 0 until chapters.size - 1) chapters[currentIdx + 1].id else null

            if (pagesResult.isSuccess && pagesResult.getOrDefault(emptyList()).isNotEmpty()) {
                val pages = pagesResult.getOrDefault(emptyList())
                _uiState.update {
                    it.copy(
                        comic = comicResult.getOrNull(),
                        currentChapter = currentChapter,
                        pages = pages,
                        prevChapterId = prevChapterId,
                        nextChapterId = nextChapterId,
                        currentPageIndex = 1,
                        totalPages = pages.size,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tidak dapat memuat halaman chapter ini."
                    )
                }
            }
        }
    }

    fun toggleControls() {
        _uiState.update { it.copy(isControlsVisible = !it.isControlsVisible) }
    }

    fun updateCurrentPage(pageIndex: Int) {
        if (pageIndex in 1.._uiState.value.totalPages) {
            _uiState.update { it.copy(currentPageIndex = pageIndex) }
            viewModelScope.launch {
                val state = _uiState.value
                val chapter = state.currentChapter
                repository.saveReadingProgress(
                    com.aziz.comicverse.domain.model.ReadingProgress(
                        comicId = comicId,
                        chapterId = chapter?.id ?: initialChapterId,
                        chapterTitle = chapter?.title ?: "Chapter",
                        pageIndex = pageIndex,
                        totalPages = state.totalPages
                    )
                )
            }
        }
    }

    companion object {
        fun provideFactory(comicId: String, chapterId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReaderViewModel(comicId, chapterId) as T
                }
            }
    }
}
