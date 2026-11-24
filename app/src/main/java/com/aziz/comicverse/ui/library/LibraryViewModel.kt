package com.aziz.comicverse.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.remote.LegalDemoComicProvider
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
        observeHistory()
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun removeFavorite(comicId: String) {
        viewModelScope.launch {
            repository.removeFavorite(comicId)
        }
    }

    fun deleteHistory(comicId: String) {
        viewModelScope.launch {
            repository.deleteReadingProgress(comicId)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoriteComics().collect { favorites ->
                _uiState.update { it.copy(favoriteComics = favorites) }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getReadingHistory().collect { history ->
                val map = LegalDemoComicProvider.demoComics.associateBy { it.id }
                _uiState.update {
                    it.copy(
                        readingHistory = history,
                        historyComicsMap = map
                    )
                }
            }
        }
    }
}
