package com.aziz.comicverse.ui.detail

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

class ComicDetailViewModel(
    private val comicId: String,
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComicDetailUiState())
    val uiState: StateFlow<ComicDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        observeFavoriteStatus()
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            repository.isFavorite(comicId).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val comicResult = repository.getComicDetail(comicId)
            val chaptersResult = repository.getComicChapters(comicId)

            if (comicResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        comic = comicResult.getOrNull(),
                        chapters = chaptersResult.getOrDefault(emptyList()),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tidak dapat memuat detail komik."
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val comic = _uiState.value.comic ?: return@launch
            repository.toggleFavorite(comic)
        }
    }

    companion object {
        fun provideFactory(comicId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ComicDetailViewModel(comicId) as T
                }
            }
    }
}
