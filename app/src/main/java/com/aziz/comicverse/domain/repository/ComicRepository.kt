package com.aziz.comicverse.domain.repository

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ComicPage
import com.aziz.comicverse.domain.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

interface ComicRepository {
    // Remote & Catalog
    suspend fun getTrendingComics(): Result<List<Comic>>
    suspend fun getPopularComics(): Result<List<Comic>>
    suspend fun getLatestManga(): Result<List<Comic>>
    suspend fun getCompletedManga(): Result<List<Comic>>
    suspend fun getRankedManga(period: String = "all_time"): Result<List<Comic>>
    suspend fun searchComics(query: String, category: String? = null): Result<List<Comic>>
    suspend fun getGenres(): List<String>
    suspend fun getComicDetail(id: String): Result<Comic>
    suspend fun getComicChapters(comicId: String): Result<List<ComicChapter>>
    suspend fun getChapterPages(comicId: String, chapterId: String): Result<List<ComicPage>>

    // Local Persistence (Room)
    fun getFavoriteComics(): Flow<List<Comic>>
    fun isFavorite(comicId: String): Flow<Boolean>
    suspend fun toggleFavorite(comic: Comic)
    suspend fun removeFavorite(comicId: String)
    fun getReadingHistory(): Flow<List<ReadingProgress>>
    fun getReadingProgress(comicId: String): Flow<ReadingProgress?>
    suspend fun saveReadingProgress(progress: ReadingProgress)
    suspend fun deleteReadingProgress(comicId: String)
}
