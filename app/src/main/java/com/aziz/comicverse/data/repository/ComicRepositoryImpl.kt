package com.aziz.comicverse.data.repository

import com.aziz.comicverse.ComicVerseApp
import com.aziz.comicverse.data.local.ComicDatabase
import com.aziz.comicverse.data.local.entity.ComicEntity
import com.aziz.comicverse.data.local.entity.ReadingProgressEntity
import com.aziz.comicverse.data.mapper.ComicMapper
import com.aziz.comicverse.data.remote.LegalDemoComicProvider
import com.aziz.comicverse.data.remote.NetworkClient
import com.aziz.comicverse.data.remote.mangadex.MangaDexApiService
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
    private val mangaDexApi: MangaDexApiService = NetworkClient.mangaDexApi,
    private val database: ComicDatabase = ComicVerseApp.instance.database
) : ComicRepository {

    override suspend fun getTrendingComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = mangaDexApi.getPopularManga(limit = 10, order = "followedCount")
            val apiComics = response.data?.map { ComicMapper.mapMangaDexItemToComic(it) } ?: emptyList()

            val combined = (LegalDemoComicProvider.demoComics + apiComics).distinctBy { it.id }
            Result.success(combined)
        } catch (e: Exception) {
            // Try Kitsu or local fallback if MangaDex unreachable
            try {
                val kitsuResp = NetworkClient.kitsuApi.getTrendingManga()
                val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
                Result.success((LegalDemoComicProvider.demoComics + kitsuComics).distinctBy { it.id })
            } catch (ignored: Exception) {
                Result.success(LegalDemoComicProvider.demoComics)
            }
        }
    }

    override suspend fun getPopularComics(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = mangaDexApi.getPopularManga(limit = 20)
            val apiComics = response.data?.map { ComicMapper.mapMangaDexItemToComic(it) } ?: emptyList()
            Result.success(apiComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (e: Exception) {
            try {
                val kitsuResp = NetworkClient.kitsuApi.getPopularManga(limit = 20)
                val kitsuComics = kitsuResp.data?.map { ComicMapper.mapKitsuItemToComic(it) } ?: emptyList()
                Result.success(kitsuComics.ifEmpty { LegalDemoComicProvider.demoComics })
            } catch (ignored: Exception) {
                Result.success(LegalDemoComicProvider.demoComics)
            }
        }
    }

    override suspend fun getLatestManga(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = mangaDexApi.getLatestManga(limit = 18)
            val apiComics = response.data?.map { ComicMapper.mapMangaDexItemToComic(it) } ?: emptyList()
            Result.success(apiComics.ifEmpty { LegalDemoComicProvider.demoComics })
        } catch (e: Exception) {
            Result.success(LegalDemoComicProvider.demoComics)
        }
    }

    override suspend fun getCompletedManga(): Result<List<Comic>> = withContext(Dispatchers.IO) {
        try {
            val response = mangaDexApi.getCompletedManga(limit = 12)
            val apiComics = response.data?.map { ComicMapper.mapMangaDexItemToComic(it) } ?: emptyList()
            Result.success(apiComics.ifEmpty { LegalDemoComicProvider.demoComics.filter { it.status.equals("Completed", ignoreCase = true) } })
        } catch (e: Exception) {
            val fallback = LegalDemoComicProvider.demoComics.filter { it.status.equals("Completed", ignoreCase = true) }
            Result.success(fallback.ifEmpty { LegalDemoComicProvider.demoComics })
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

            val searchTerm = query.ifBlank { category ?: "manga" }
            val response = mangaDexApi.searchManga(title = searchTerm, limit = 20)
            val apiComics = response.data?.map { ComicMapper.mapMangaDexItemToComic(it) } ?: emptyList()

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
        val demoComic = LegalDemoComicProvider.demoComics.find { it.id == id }
        if (demoComic != null) {
            return@withContext Result.success(demoComic)
        }

        try {
            val response = mangaDexApi.getMangaById(id)
            val item = response.data
            if (item != null) {
                Result.success(ComicMapper.mapMangaDexItemToComic(item))
            } else {
                Result.failure(Exception("Manga not found"))
            }
        } catch (e: Exception) {
            // Fallback to Kitsu
            try {
                val kitsu = NetworkClient.kitsuApi.getMangaById(id)
                kitsu.data?.let { Result.success(ComicMapper.mapKitsuItemToComic(it)) }
                    ?: Result.failure(e)
            } catch (ignored: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getComicChapters(comicId: String): Result<List<ComicChapter>> = withContext(Dispatchers.IO) {
        val demoChapters = LegalDemoComicProvider.getChapters(comicId)
        if (demoChapters.isNotEmpty()) {
            return@withContext Result.success(demoChapters)
        }

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
                // Filter chapters that have pages (exclude external redirect links)
                (it.attributes?.pages ?: 0) > 0 && it.attributes?.externalUrl.isNullOrBlank()
            }?.map {
                ComicMapper.mapMangaDexChapter(it, comicId)
            } ?: emptyList()

            if (chapters.isNotEmpty()) {
                // Group by chapter number to avoid duplicate translations, prioritizing Indonesian [ID] then English [EN]
                val distinctChapters = chapters.groupBy { it.chapterNumber }
                    .map { (_, group) ->
                        group.find { it.title.startsWith("[ID]") }
                            ?: group.find { it.title.startsWith("[EN]") }
                            ?: group.maxByOrNull { it.pageCount }
                            ?: group.first()
                    }
                    .sortedBy { it.chapterNumber.toDoubleOrNull() ?: 0.0 }
                return@withContext Result.success(distinctChapters)
            }
        } catch (e: Exception) {
            // Fall through to check if demo comic
        }

        if (comicId.startsWith("demo-")) {
            return@withContext Result.success(LegalDemoComicProvider.getChapters(comicId))
        }

        Result.failure(Exception("Chapter untuk komik ini tidak tersedia di server MangaDex."))
    }

    override suspend fun getChapterPages(comicId: String, chapterId: String): Result<List<ComicPage>> = withContext(Dispatchers.IO) {
        if (comicId.startsWith("demo-") || chapterId.startsWith("pc-") || chapterId.startsWith("sk-") || chapterId.startsWith("cn-")) {
            val demoPages = LegalDemoComicProvider.getPages(chapterId)
            if (demoPages.isNotEmpty()) {
                return@withContext Result.success(demoPages)
            }
        }

        try {
            // Fetch live pages from MangaDex At-Home server!
            val atHome = mangaDexApi.getAtHomeServer(chapterId)
            val baseUrl = atHome.baseUrl
            val hash = atHome.chapter?.hash

            // Use data-saver for 10x-20x faster mobile loading
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

        Result.failure(Exception("Halaman untuk chapter ini tidak ditemukan di MangaDex."))
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

    // Room Reading Progress / History
    override fun getReadingHistory(): Flow<List<ReadingProgress>> {
        return database.readingProgressDao().getAllProgress().map { list ->
            list.map {
                ReadingProgress(
                    comicId = it.comicId,
                    chapterId = it.chapterId,
                    chapterTitle = it.chapterTitle,
                    pageIndex = it.pageIndex,
                    totalPages = it.totalPages,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    override suspend fun saveReadingProgress(progress: ReadingProgress): Unit = withContext(Dispatchers.IO) {
        val comic = LegalDemoComicProvider.demoComics.find { it.id == progress.comicId }
        val title = comic?.title ?: "Comic"
        val cover = comic?.coverUrl ?: ""

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
        Unit
    }
}
