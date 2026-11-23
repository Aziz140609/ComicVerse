package com.aziz.comicverse.ui.rankings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aziz.comicverse.data.repository.ComicRepositoryImpl
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingsViewModel(
    private val repository: ComicRepository = ComicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingsUiState())
    val uiState: StateFlow<RankingsUiState> = _uiState.asStateFlow()

    init {
        loadRankings(0)
    }

    fun selectTab(tabIndex: Int) {
        if (_uiState.value.selectedTab == tabIndex && _uiState.value.comics.isNotEmpty()) return
        _uiState.update { it.copy(selectedTab = tabIndex) }
        loadRankings(tabIndex)
    }

    fun loadRankings(tabIndex: Int = _uiState.value.selectedTab) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val period = if (tabIndex == 0) "all_time" else "weekly"
            val result = repository.getRankedManga(period)

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
                        errorMessage = "Gagal memuat daftar peringkat manga."
                    )
                }
            }
        }
    }
}
