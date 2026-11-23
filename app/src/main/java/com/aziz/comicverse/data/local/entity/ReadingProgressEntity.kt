package com.aziz.comicverse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val comicId: String,
    val comicTitle: String,
    val comicCoverUrl: String,
    val chapterId: String,
    val chapterTitle: String,
    val pageIndex: Int,
    val totalPages: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
