package com.aziz.comicverse.domain.repository

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ComicPage

interface ComicRepository {
    suspend fun getTrendingComics(): Result<List<Comic>>
    suspend fun getPopularComics(): Result<List<Comic>>
    suspend fun searchComics(query: String, category: String? = null): Result<List<Comic>>
    suspend fun getComicDetail(id: String): Result<Comic>
    suspend fun getComicChapters(comicId: String): Result<List<ComicChapter>>
    suspend fun getChapterPages(comicId: String, chapterId: String): Result<List<ComicPage>>
}
