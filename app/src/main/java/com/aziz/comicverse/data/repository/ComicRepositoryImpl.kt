package com.aziz.comicverse.data.repository

import com.aziz.comicverse.data.mapper.ComicMapper
import com.aziz.comicverse.data.remote.LegalDemoComicProvider
import com.aziz.comicverse.data.remote.NetworkClient
import com.aziz.comicverse.data.remote.kitsu.KitsuApiService
import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ComicPage
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComicRepositoryImpl(
    private val apiService: KitsuApiService = NetworkClient.kitsuApi
) : ComicRepository {

    override suspend fun getTrendingComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTrendingManga()
            val apiComics = response.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()

            // Combine legal demo featured comics with trending API comics
            val combined = (LegalDemoComicProvider.demoComics + apiComics).distinctBy { it.id }
            Result.success(combined)
        } catch (e: Exception) {
            // Offline fallback to rich legal demo comics
            Result.success(LegalDemoComicProvider.demoComics)
        }
    }

    override suspend fun getPopularComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPopularManga(limit = 20)
            val apiComics = response.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            Result.success(apiComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (e: Exception) {
            Result.success(LegalDemoComicProvider.demoComics)
        }
    }

    override suspend fun searchComics(query: String, category: String?): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank() && category.isNullOrBlank()) {
                return@withContext getPopularComics()
            }

            val localMatches = LegalDemoComicProvider.demoComics.filter { comic ->
                comic.title.contains(query, ignoreCase = true) ||
                comic.description.contains(query, ignoreCase = true) ||
                comic.genres.any { it.contains(query, ignoreCase = true) }
            }

            val response = apiService.searchManga(query = query, category = category, limit = 20)
            val apiComics = response.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()

            val combined = (localMatches + apiComics).distinctBy { it.id }
            Result.success(combined)
        } catch (e: Exception) {
            val fallbackMatches = LegalDemoComicProvider.demoComics.filter { comic ->
                comic.title.contains(query, ignoreCase = true) ||
                comic.genres.any { it.contains(query, ignoreCase = true) }
            }
            Result.success(fallbackMatches)
        }
    }

    override suspend fun getComicDetail(id: String): Result<Comic> = withContext(Dispatchers.IO) {
        // Check if it's a demo comic
        val demoComic = LegalDemoComicProvider.demoComics.find { it.id == id }
        if (demoComic != null) {
            return@withContext Result.success(demoComic)
        }

        try {
            val response = apiService.getMangaById(id)
            val item = response.data
            if (item != null) {
                Result.success(ComicMapper.mapKitsuItemToComic(item))
            } else {
                Result.failure(Exception("Comic not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getComicChapters(comicId: String): Result<List<ComicChapter>> = withContext(Dispatchers.IO) {
        val demoChapters = LegalDemoComicProvider.getChapters(comicId)
        if (demoChapters.isNotEmpty()) {
            return@withContext Result.success(demoChapters)
        }

        // For API comics where external full chapters are copyrighted, provide legal reader preview chapters
        val previewChapters = listOf(
            ComicChapter(
                id = "${comicId}-ch-1",
                comicId = comicId,
                title = "Chapter 1: Official Preview",
                chapterNumber = "1",
                releaseDate = "Preview",
                pageCount = 6
            ),
            ComicChapter(
                id = "${comicId}-ch-2",
                comicId = comicId,
                title = "Chapter 2: Story Highlights",
                chapterNumber = "2",
                releaseDate = "Preview",
                pageCount = 5
            )
        )
        Result.success(previewChapters)
    }

    override suspend fun getChapterPages(comicId: String, chapterId: String): Result<List<ComicPage>> = withContext(Dispatchers.IO) {
        val pages = LegalDemoComicProvider.getPages(chapterId)
        Result.success(pages)
    }
}
