package com.aziz.comicverse.ui.explore

import com.aziz.comicverse.domain.model.Comic

val DEFAULT_EXPLORE_GENRES = listOf(
    "All", "Action", "Adventure", "Comedy", "Demons", "Drama", "Ecchi",
    "Fantasy", "Game", "Harem", "Historical", "Horror", "Isekai", "Josei",
    "Magic", "Martial Arts", "Mature", "Mecha", "Military", "Mystery",
    "Psychological", "Reincarnation", "Romance", "School Life", "Sci-Fi",
    "Seinen", "Shoujo", "Shounen", "Slice of Life", "Sports", "Superhero",
    "Supernatural", "Thriller", "Tragedy", "Wuxia"
)

data class ExploreUiState(
    val query: String = "",
    val selectedGenre: String? = null,
    val availableGenres: List<String> = DEFAULT_EXPLORE_GENRES,
    val comics: List<Comic> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
