package com.aziz.comicverse.data.repository

import com.aziz.comicverse.ComicVerseApp
import com.aziz.comicverse.data.local.ComicDatabase
import com.aziz.comicverse.data.local.entity.ComicEntity
import com.aziz.comicverse.data.local.entity.ReadingProgressEntity
import com.aziz.comicverse.data.mapper.ComicMapper
import com.aziz.comicverse.data.remote.LegalDemoComicProvider
import com.aziz.comicverse.data.remote.NetworkClient
import com.aziz.comicverse.data.remote.mangadex.MangaDexApiService
import com.aziz.comicverse.data.remote.sanka.SankaApiService
import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ComicPage
import com.aziz.comicverse.domain.model.ReadingProgress
import com.aziz.comicverse.domain.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ComicRepositoryImpl(
    private val sankaApi: SankaApiService = NetworkClient.sankaApi,
    private val mangaDexApi: MangaDexApiService = NetworkClient.mangaDexApi,
    private val database: ComicDatabase = ComicVerseApp.instance.database
) : ComicRepository {

    private val detailCache = java.util.concurrent.ConcurrentHashMap<String, com.aziz.comicverse.data.remote.sanka.SankaDetailData>()

    private suspend fun fetchSankaDetail(id: String): com.aziz.comicverse.data.remote.sanka.SankaDetailData? {
        detailCache[id]?.let { return it }
        return try {
            val resp = sankaApi.getComicDetail(id)
            resp.detail?.also { detailCache[id] = it }
        } catch (e: Exception) {
            android.util.Log.e("ComicRepository", "Failed to fetch Sanka detail for $id: ${e.message}", e)
            null
        }
    }

    override suspend fun getTrendingComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getTopComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                val combined = (LegalDemoComicProvider.demoComics + comics).distinctBy { it.id }
                return@withContext Result.success(combined)
            }
        } catch (_: Exception) {}

        Result.success(LegalDemoComicProvider.demoComics)
    }

    override suspend fun getPopularComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getPopularComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        Result.success(LegalDemoComicProvider.demoComics)
    }

    override suspend fun getLatestManga(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getLatestComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        Result.success(LegalDemoComicProvider.demoComics)
    }

    override suspend fun getCompletedManga(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getTopComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        val fallback = LegalDemoComicProvider.demoComics.filter { it.status.equals("Completed", ignoreCase = true) }
        Result.success(fallback.ifEmpty { LegalDemoComicProvider.demoComics })
    }

    override suspend fun getRankedManga(period: String): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = if (period.equals("weekly", ignoreCase = true)) {
                sankaApi.getPopularComics()
            } else {
                sankaApi.getTopComics()
            }
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        Result.success(LegalDemoComicProvider.demoComics)
    }

    override suspend fun searchComics(query: String, category: String?): Result<List<Comic>> = withContext(Dispatchers.IO) {
        val localMatches = LegalDemoComicProvider.demoComics.filter { comic ->
            comic.title.contains(query, ignoreCase = true) ||
            comic.description.contains(query, ignoreCase = true) ||
            comic.genres.any { it.contains(query, ignoreCase = true) }
        }

        if (query.isBlank() && category.isNullOrBlank()) {
            return@withContext getPopularComics()
        }

        val searchTerm = query.ifBlank { category ?: "manga" }

        // Sanka Indo comics search
        try {
            val sankaResp = sankaApi.searchComics(searchTerm)
            val sankaComics = sankaResp.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (sankaComics.isNotEmpty()) {
                val combined = (localMatches + sankaComics).distinctBy { it.id }
                return@withContext Result.success(combined)
            }
        } catch (_: Exception) {}

        Result.success(localMatches)
    }

    override suspend fun getComicDetail(id: String): Result<Comic> = withContext(Dispatchers.IO) {
        val demoComic = LegalDemoComicProvider.demoComics.find { it.id == id }
        if (demoComic != null) {
            return@withContext Result.success(demoComic)
        }

        // Try Sanka Detail (slug-based Indonesian comics)
        val sankaDetail = fetchSankaDetail(id)
        if (sankaDetail != null) {
            return@withContext Result.success(ComicMapper.mapSankaDetailToComic(id, sankaDetail))
        }

        Result.failure(Exception("Komik tidak ditemukan"))
    }

    override suspend fun getComicChapters(comicId: String): Result<List<ComicChapter>> = withContext(Dispatchers.IO) {
        val demoChapters = LegalDemoComicProvider.getChapters(comicId)
        if (demoChapters.isNotEmpty()) {
            return@withContext Result.success(demoChapters)
        }

        // 1. Primary: Sanka detail has complete Indonesian chapters!
        val sankaDetail = fetchSankaDetail(comicId)
        val sankaChapters = sankaDetail?.chapters
        if (!sankaChapters.isNullOrEmpty()) {
            val mappedChapters = sankaChapters.map { ComicMapper.mapSankaChapter(it, comicId) }
            return@withContext Result.success(mappedChapters)
        }

        Result.failure(Exception("Daftar chapter belum tersedia untuk komik ini."))
    }

    override suspend fun getChapterPages(comicId: String, chapterId: String): Result<List<ComicPage>> = withContext(Dispatchers.IO) {
        if (comicId.startsWith("demo-") || chapterId.startsWith("pc-") || chapterId.startsWith("sk-") || chapterId.startsWith("cn-")) {
            val demoPages = LegalDemoComicProvider.getPages(chapterId)
            if (demoPages.isNotEmpty()) {
                return@withContext Result.success(demoPages)
            }
        }

        // Sanka chapter detail (returns full image array from Indonesian comic servers!)
        try {
            val sankaChapter = sankaApi.getChapterDetail(chapterId)
            val images = sankaChapter.images
            if (!images.isNullOrEmpty()) {
                val pages = images.mapIndexed { idx, url ->
                    ComicPage(
                        index = idx + 1,
                        imageUrl = url
                    )
                }
                return@withContext Result.success(pages)
            }
        } catch (_: Exception) {}

        Result.failure(Exception("Halaman untuk chapter ini tidak ditemukan."))
    }

    // Room Favorites
    override fun getFavoriteComics(): Flow<List<Comic>> {
        return database.comicDao().getFavoriteComics().map { entities ->
            entities.map { entity ->
                Comic(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    coverUrl = entity.coverUrl,
                    author = entity.author,
                    genres = entity.genres.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    status = entity.status,
                    rating = entity.rating,
                    isDemoContent = entity.isDemoContent
                )
            }
        }
    }

    override fun isFavorite(comicId: String): Flow<Boolean> {
        return database.comicDao().isFavorite(comicId).map { it == true }
    }

    override suspend fun toggleFavorite(comic: Comic): Unit = withContext(Dispatchers.IO) {
        val current = database.comicDao().getComicById(comic.id)
        if (current == null) {
            database.comicDao().insertOrUpdate(
                ComicEntity(
                    id = comic.id,
                    title = comic.title,
                    description = comic.description,
                    coverUrl = comic.coverUrl,
                    author = comic.author,
                    genres = comic.genres.joinToString(","),
                    status = comic.status,
                    rating = comic.rating,
                    isFavorite = true,
                    isDemoContent = comic.isDemoContent
                )
            )
        } else {
            database.comicDao().updateFavorite(comic.id, !current.isFavorite)
        }
        Unit
    }

    // Room Reading Progress / History (Max 10 items, oldest removed)
    override fun getReadingHistory(): Flow<List<ReadingProgress>> {
        return database.readingProgressDao().getAllProgress().map { list ->
            list.map {
                ReadingProgress(
                    comicId = it.comicId,
                    chapterId = it.chapterId,
                    chapterTitle = it.chapterTitle,
                    pageIndex = it.pageIndex,
                    totalPages = it.totalPages,
                    comicTitle = it.comicTitle,
                    comicCoverUrl = it.comicCoverUrl,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    override suspend fun saveReadingProgress(progress: ReadingProgress): Unit = withContext(Dispatchers.IO) {
        val title = if (progress.comicTitle.isNotBlank()) {
            progress.comicTitle
        } else {
            database.comicDao().getComicById(progress.comicId)?.title
                ?: LegalDemoComicProvider.demoComics.find { it.id == progress.comicId }?.title
                ?: "Comic"
        }
        val cover = if (progress.comicCoverUrl.isNotBlank()) {
            progress.comicCoverUrl
        } else {
            database.comicDao().getComicById(progress.comicId)?.coverUrl
                ?: LegalDemoComicProvider.demoComics.find { it.id == progress.comicId }?.coverUrl
                ?: ""
        }

        database.readingProgressDao().insertOrUpdateProgress(
            ReadingProgressEntity(
                comicId = progress.comicId,
                comicTitle = title,
                comicCoverUrl = cover,
                chapterId = progress.chapterId,
                chapterTitle = progress.chapterTitle,
                pageIndex = progress.pageIndex,
                totalPages = progress.totalPages,
                updatedAt = progress.updatedAt
            )
        )
        // Ensure maximum 10 items; automatically trim/delete the oldest records
        database.readingProgressDao().trimOldProgress()
        Unit
    }

    override suspend fun deleteReadingProgress(comicId: String): Unit = withContext(Dispatchers.IO) {
        database.readingProgressDao().deleteProgress(comicId)
        Unit
    }
}
