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

    private suspend fun resolveLegacyIdToSlug(id: String): String? {
        val roomEntity = database.comicDao().getComicById(id) ?: return null
        val fullTitle = roomEntity.title
        val candidates = listOf(fullTitle) + fullTitle
            .replace(':', '|')
            .replace('-', '|')
            .replace('(', '|')
            .replace(')', '|')
            .split('|')
            .map { it.trim() }
            .filter { it.length >= 3 }

        for (candidate in candidates) {
            try {
                val searchResp = sankaApi.searchComics(candidate)
                val list = searchResp.komikList ?: continue
                if (list.isNotEmpty()) {
                    val match = list.find {
                        it.title?.contains(candidate, ignoreCase = true) == true ||
                        candidate.contains(it.title ?: "", ignoreCase = true) ||
                        it.slug?.contains(candidate.replace(" ", "-"), ignoreCase = true) == true
                    } ?: list.first()

                    val targetSlug = match.slug
                    if (!targetSlug.isNullOrBlank()) {
                        database.comicDao().insertOrUpdate(roomEntity.copy(id = targetSlug))
                        return targetSlug
                    }
                }
            } catch (_: Exception) {}
        }
        return null
    }

    private suspend fun fetchSankaDetail(id: String): com.aziz.comicverse.data.remote.sanka.SankaDetailData? {
        detailCache[id]?.let { return it }

        // If ID is numeric (like "37280" from Kitsu) or UUID, resolve to slug instead of calling detail API directly
        val isLegacyId = id.all { it.isDigit() } || (id.length >= 30 && id.contains("-"))
        val actualSlug = if (isLegacyId) {
            resolveLegacyIdToSlug(id) ?: id
        } else {
            id
        }

        // If still purely numeric after resolution, avoid sending GET request that triggers HTTP 500
        if (actualSlug.all { it.isDigit() }) {
            return null
        }

        detailCache[actualSlug]?.let { return it }

        return try {
            val resp = sankaApi.getComicDetail(actualSlug)
            resp.detail?.also {
                detailCache[actualSlug] = it
                if (actualSlug != id) {
                    detailCache[id] = it
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ComicRepository", "Failed to fetch Sanka detail for $actualSlug: ${e.message}")
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

        // 1. Try Sanka Detail directly by ID/slug
        val sankaDetail = fetchSankaDetail(id)
        if (sankaDetail != null) {
            return@withContext Result.success(ComicMapper.mapSankaDetailToComic(id, sankaDetail))
        }

        // 2. Try normalized slug if ID contains spaces, uppercase or special chars
        val normalizedSlug = id.trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
        if (normalizedSlug.isNotBlank() && normalizedSlug != id) {
            val normalizedDetail = fetchSankaDetail(normalizedSlug)
            if (normalizedDetail != null) {
                return@withContext Result.success(ComicMapper.mapSankaDetailToComic(id, normalizedDetail))
            }
        }

        // 3. Check Room Database! (For comics favorited/saved in Library with old IDs)
        val roomEntity = database.comicDao().getComicById(id)
        if (roomEntity != null) {
            // Try to find Sanka slug by searching the saved comic's title (e.g. Kimetsu no Yaiba)
            val cleanTitle = roomEntity.title.replace(Regex("(?i)\\[.*?\\]|\\(.*?\\)"), "").trim()
            if (cleanTitle.isNotBlank()) {
                try {
                    val searchResp = sankaApi.searchComics(cleanTitle)
                    val matchSlug = searchResp.komikList?.firstOrNull()?.slug
                    if (!matchSlug.isNullOrBlank()) {
                        val resolvedDetail = fetchSankaDetail(matchSlug)
                        if (resolvedDetail != null) {
                            return@withContext Result.success(ComicMapper.mapSankaDetailToComic(matchSlug, resolvedDetail))
                        }
                    }
                } catch (_: Exception) {}
            }

            // Return saved comic from Room database as guaranteed offline fallback!
            val fallbackComic = Comic(
                id = roomEntity.id,
                title = roomEntity.title,
                description = roomEntity.description,
                coverUrl = roomEntity.coverUrl,
                author = roomEntity.author,
                genres = roomEntity.genres.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                status = roomEntity.status,
                rating = roomEntity.rating,
                chapterCount = 1,
                isDemoContent = roomEntity.isDemoContent
            )
            return@withContext Result.success(fallbackComic)
        }

        Result.failure(Exception("Komik tidak ditemukan"))
    }

    override suspend fun getComicChapters(comicId: String): Result<List<ComicChapter>> = withContext(Dispatchers.IO) {
        val demoChapters = LegalDemoComicProvider.getChapters(comicId)
        if (demoChapters.isNotEmpty()) {
            return@withContext Result.success(demoChapters)
        }

        // 1. Primary: Sanka detail by comicId directly
        var sankaDetail = fetchSankaDetail(comicId)

        // 2. Try normalized slug
        if (sankaDetail == null) {
            val normalized = comicId.trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
            if (normalized.isNotBlank() && normalized != comicId) {
                sankaDetail = fetchSankaDetail(normalized)
            }
        }

        // 3. If comic was saved in Room with old ID, search Sanka by title
        if (sankaDetail == null) {
            val roomEntity = database.comicDao().getComicById(comicId)
            val cleanTitle = roomEntity?.title?.replace(Regex("(?i)\\[.*?\\]|\\(.*?\\)"), "")?.trim()
            if (!cleanTitle.isNullOrBlank()) {
                try {
                    val searchResp = sankaApi.searchComics(cleanTitle)
                    val matchSlug = searchResp.komikList?.firstOrNull()?.slug
                    if (!matchSlug.isNullOrBlank()) {
                        sankaDetail = fetchSankaDetail(matchSlug)
                    }
                } catch (_: Exception) {}
            }
        }

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

    override fun getReadingProgress(comicId: String): Flow<ReadingProgress?> {
        return database.readingProgressDao().getAllProgress().map { list ->
            list.find {
                it.comicId == comicId ||
                it.comicId.equals(comicId.replace("-", " "), ignoreCase = true) ||
                it.comicTitle.equals(comicId.replace("-", " "), ignoreCase = true)
            }?.let {
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
