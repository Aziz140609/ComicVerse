package com.aziz.comicverse.domain.model

data class ComicChapter(
    val id: String,
    val comicId: String,
    val title: String,
    val chapterNumber: String,
    val releaseDate: String? = null,
    val pageCount: Int = 0
)
