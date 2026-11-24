package com.aziz.comicverse.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialComics()
    }

    fun loadInitialComics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getPopularComics()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        comics = result.getOrDefault(emptyList()),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat katalog komik."
                    )
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // Debounce
            executeSearch(newQuery, _uiState.value.selectedGenre)
        }
    }

    fun selectGenre(genre: String?) {
        val nextGenre = if (_uiState.value.selectedGenre == genre) null else genre
        _uiState.update { it.copy(selectedGenre = nextGenre) }
        executeSearch(_uiState.value.query, nextGenre)
    }

    private fun executeSearch(query: String, genre: String?) {
        val activeGenre = if (genre.equals("All", ignoreCase = true)) null else genre
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.searchComics(query = query, category = activeGenre)
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _uiState.update {
                    it.copy(comics = list, isLoading = false, errorMessage = null)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Tidak dapat memuat hasil pencarian.")
                }
            }
        }
    }
}
