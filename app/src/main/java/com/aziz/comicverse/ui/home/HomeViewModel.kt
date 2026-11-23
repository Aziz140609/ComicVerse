package com.aziz.comicverse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.repository.ComicRepository
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
                    val comics = history.mapNotNull { progress ->
                        repository.getComicDetail(progress.comicId).getOrNull()
                    }.distinctBy { it.id }
                    _uiState.update { it.copy(continueReading = comics) }
                } else {
                    _uiState.update { it.copy(continueReading = emptyList()) }
                }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val trendingResult = repository.getTrendingComics()
            val popularResult = repository.getPopularComics()

            if (trendingResult.isSuccess || popularResult.isSuccess) {
                val trending = trendingResult.getOrDefault(emptyList())
                val popular = popularResult.getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        featuredComics = trending.take(5),
                        popularComics = popular.ifEmpty { trending },
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
