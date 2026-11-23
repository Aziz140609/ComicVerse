package com.aziz.comicverse.domain.model

data class Comic(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val author: String,
    val genres: List<String>,
    val status: String,
    val rating: Double? = null,
    val chapterCount: Int? = null,
    val isDemoContent: Boolean = false
)
