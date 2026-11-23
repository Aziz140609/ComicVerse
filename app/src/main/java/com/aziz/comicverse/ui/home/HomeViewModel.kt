package com.aziz.comicverse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getReadingHistory().collect { history ->
                if (history.isNotEmpty()) {
                    val items = history.mapNotNull { progress ->
                        val comic = repository.getComicDetail(progress.comicId).getOrNull()
                        if (comic != null) ContinueReadingItem(comic, progress) else null
                    }.distinctBy { it.comic.id }
                    _uiState.update { it.copy(continueReading = items) }
                } else {
                    _uiState.update { it.copy(continueReading = emptyList()) }
                }
            }
        }
    }

    fun toggleNewUpdateExpanded() {
        _uiState.update { it.copy(isNewUpdateExpanded = !it.isNewUpdateExpanded) }
    }

    fun toggleCompletedExpanded() {
        _uiState.update { it.copy(isCompletedExpanded = !it.isCompletedExpanded) }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val latestDeferred = async(Dispatchers.IO) { repository.getLatestManga() }
            val hotDeferred = async(Dispatchers.IO) { repository.getPopularComics() }
            val completedDeferred = async(Dispatchers.IO) { repository.getCompletedManga() }

            val latestResult = latestDeferred.await()
            val hotResult = hotDeferred.await()
            val completedResult = completedDeferred.await()

            if (latestResult.isSuccess || hotResult.isSuccess || completedResult.isSuccess) {
                val latest = latestResult.getOrDefault(emptyList())
                val hot = hotResult.getOrDefault(emptyList())
                val completed = completedResult.getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        newUpdateComics = latest,
                        hotComics = hot,
                        completedComics = completed,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat katalog komik. Periksa koneksi internet Anda."
                    )
                }
            }
        }
    }
}
