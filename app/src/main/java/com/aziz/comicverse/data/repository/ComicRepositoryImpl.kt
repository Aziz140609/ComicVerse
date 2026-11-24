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
import com.aziz.comicverse.preferences.LanguageMode
import com.aziz.comicverse.preferences.UserPreferencesRepository
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

        // Fallback to Kitsu
        try {
            val kitsuResp = NetworkClient.kitsuApi.getTrendingManga()
            val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            Result.success((LegalDemoComicProvider.demoComics + kitsuComics).distinctBy { it.id })
        } catch (_: Exception) {
            Result.success(LegalDemoComicProvider.demoComics)
        }
    }

    override suspend fun getPopularComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getPopularComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        // Fallback to Kitsu
        try {
            val kitsuResp = NetworkClient.kitsuApi.getPopularManga(limit = 20)
            val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            Result.success(kitsuComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (_: Exception) {
            Result.success(LegalDemoComicProvider.demoComics)
        }
    }

    override suspend fun getLatestManga(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getLatestComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        // Fallback to Kitsu
        try {
            val kitsuResp = NetworkClient.kitsuApi.getLatestManga(limit = 18)
            val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            Result.success(kitsuComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (_: Exception) {
            Result.success(LegalDemoComicProvider.demoComics)
        }
    }

    override suspend fun getCompletedManga(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = sankaApi.getTopComics()
            val comics = response.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (comics.isNotEmpty()) {
                return@withContext Result.success(comics)
            }
        } catch (_: Exception) {}

        // Fallback to Kitsu
        try {
            val kitsuResp = NetworkClient.kitsuApi.getCompletedManga(limit = 18)
            val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            Result.success(kitsuComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (_: Exception) {
            val fallback = LegalDemoComicProvider.demoComics.filter { it.status.equals("Completed", ignoreCase = true) }
            Result.success(fallback.ifEmpty { LegalDemoComicProvider.demoComics })
        }
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

        // Fallback to Kitsu
        try {
            val kitsuResp = if (period.equals("weekly", ignoreCase = true)) {
                NetworkClient.kitsuApi.getTrendingManga()
            } else {
                NetworkClient.kitsuApi.getPopularManga(limit = 25)
            }
            val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            Result.success(kitsuComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (_: Exception) {
            Result.success(LegalDemoComicProvider.demoComics)
        }
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

        // Primary: Sanka Indo comics search
        try {
            val sankaResp = sankaApi.searchComics(searchTerm)
            val sankaComics = sankaResp.komikList?.map { ComicMapper.mapSankaItemToComic(it) } ?: emptyList()
            if (sankaComics.isNotEmpty()) {
                val combined = (localMatches + sankaComics).distinctBy { it.id }
                return@withContext Result.success(combined)
            }
        } catch (_: Exception) {}

        // Fallback: Kitsu search
        try {
            val kitsuResp = NetworkClient.kitsuApi.searchManga(query = searchTerm, limit = 20)
            val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
            val combined = (localMatches + kitsuComics).distinctBy { it.id }
            Result.success(combined.ifEmpty { localMatches })
        } catch (_: Exception) {
            Result.success(localMatches)
        }
    }

    override suspend fun getComicDetail(id: String): Result<Comic> = withContext(Dispatchers.IO) {
        val demoComic = LegalDemoComicProvider.demoComics.find { it.id == id }
        if (demoComic != null) {
            return@withContext Result.success(demoComic)
        }

        // 1. Try Sanka Detail (slug-based Indonesian comics)
        val sankaDetail = fetchSankaDetail(id)
        if (sankaDetail != null) {
            return@withContext Result.success(ComicMapper.mapSankaDetailToComic(id, sankaDetail))
        }

        // 2. Try Kitsu by ID
        try {
            val kitsu = NetworkClient.kitsuApi.getMangaById(id)
            kitsu.data?.let { return@withContext Result.success(ComicMapper.mapKitsuItemToComic(it)) }
        } catch (_: Exception) {}

        // 3. Try MangaDex
        try {
            val response = mangaDexApi.getMangaById(id)
            val item = response.data
            if (item != null) {
                return@withContext Result.success(ComicMapper.mapMangaDexItemToComic(item))
            }
        } catch (_: Exception) {}

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

        // 2. Fallback: MangaDex feed if UUID or title match
        try {
            var targetMangaId = comicId
            val isUuid = comicId.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))
            if (!isUuid) {
                val detail = getComicDetail(comicId).getOrNull()
                val query = detail?.title ?: comicId
                val searchResp = mangaDexApi.searchManga(title = query, limit = 5)
                val match = searchResp.data?.firstOrNull()
                if (match != null) {
                    targetMangaId = match.id
                }
            }

            val feed = mangaDexApi.getMangaFeed(mangaId = targetMangaId, limit = 100)
            val chapters = feed.data?.filter {
                (it.attributes?.pages ?: 0) > 0 && it.attributes?.externalUrl.isNullOrBlank()
            }?.map {
                ComicMapper.mapMangaDexChapter(it, comicId)
            } ?: emptyList()

            if (chapters.isNotEmpty()) {
                val distinctChapters = chapters.groupBy { it.chapterNumber }
                    .map { (_, group) ->
                        group.find { it.title.startsWith("[ID]") }
                            ?: group.find { it.title.startsWith("[EN]") }
                            ?: group.first()
                    }
                    .sortedBy { it.chapterNumber.toDoubleOrNull() ?: 0.0 }
                return@withContext Result.success(distinctChapters)
            }
        } catch (_: Exception) {}

        Result.failure(Exception("Daftar chapter belum tersedia untuk komik ini."))
    }

    override suspend fun getChapterPages(comicId: String, chapterId: String): Result<List<ComicPage>> = withContext(Dispatchers.IO) {
        if (comicId.startsWith("demo-") || chapterId.startsWith("pc-") || chapterId.startsWith("sk-") || chapterId.startsWith("cn-")) {
            val demoPages = LegalDemoComicProvider.getPages(chapterId)
            if (demoPages.isNotEmpty()) {
                return@withContext Result.success(demoPages)
            }
        }

        // 1. Primary: Sanka chapter detail (returns full image array from Indonesian comic servers!)
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

        // 2. Fallback: MangaDex At-Home server
        try {
            val atHome = mangaDexApi.getAtHomeServer(chapterId)
            val baseUrl = atHome.baseUrl
            val hash = atHome.chapter?.hash
            val useDataSaver = !atHome.chapter?.dataSaver.isNullOrEmpty()
            val filenames = if (useDataSaver) atHome.chapter?.dataSaver ?: emptyList() else atHome.chapter?.data ?: emptyList()
            val pathFolder = if (useDataSaver) "data-saver" else "data"

            if (!baseUrl.isNullOrBlank() && !hash.isNullOrBlank() && filenames.isNotEmpty()) {
                val realPages = filenames.mapIndexed { idx, filename ->
                    ComicPage(
                        index = idx + 1,
                        imageUrl = "$baseUrl/$pathFolder/$hash/$filename"
                    )
                }
                return@withContext Result.success(realPages)
            }
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("Gagal memuat halaman: ${e.localizedMessage ?: "Koneksi terputus"}"))
        }

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
