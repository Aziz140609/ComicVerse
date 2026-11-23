package com.aziz.comicverse.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.repository.ComicRepository
import com.aziz.comicverse.ui.home.ContinueReadingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReadingHistoryViewModel(
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingHistoryUiState())
    val uiState: StateFlow<ReadingHistoryUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getReadingHistory().collect { history ->
                if (history.isNotEmpty()) {
                    val items = history.take(10).map { progress ->
                        val localComic = repository.getComicDetail(progress.comicId).getOrNull()
                        val comic = localComic ?: Comic(
                            id = progress.comicId,
                            title = progress.comicTitle.ifBlank { "Manga" },
                            description = "",
                            coverUrl = progress.comicCoverUrl,
                            author = "",
                            genres = listOf("Manga"),
                            status = "Reading",
                            rating = 4.8
                        )
                        ContinueReadingItem(comic, progress)
                    }.distinctBy { it.comic.id }

                    _uiState.update {
                        it.copy(
                            historyItems = items,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            historyItems = emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun removeHistoryItem(comicId: String) {
        viewModelScope.launch {
            repository.deleteReadingProgress(comicId)
        }
    }
}
