package com.aziz.comicverse.domain.model

data class ReadingProgress(
    val comicId: String,
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int,
    val totalPages: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
