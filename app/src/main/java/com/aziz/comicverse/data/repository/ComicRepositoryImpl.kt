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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicRepositoryImpl(
    private val sankaApi: SankaApiService = NetworkClient.sankaApi,
    private val mangaDexApi: MangaDexApiService = NetworkClient.mangaDexApi,
    private val database: ComicDatabase = ComicVerseApp.instance.database
) : ComicRepository {

    init {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                database.comicDao().purgeBrokenKnyComics()
                database.readingProgressDao().purgeBrokenKnyProgress()
                val allComics = database.comicDao().getAllComicsRaw()
                for (comic in allComics) {
                    if (comic.id.all { it.isDigit() }) {
                        database.comicDao().deleteComicById(comic.id)
                        database.readingProgressDao().deleteProgress(comic.id)
                    }
                }
            } catch (_: Exception) {}
        }
    }

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
        val completedMasterpieces = listOf(
            Comic(
                id = "solo-leveling",
                title = "Solo Leveling",
                description = "Komik Manhwa completed legendaris bercerita tentang Sung Jin-Woo yang bangkit dari hunter terlemah menjadi Shadow Monarch.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/11/Komik-Solo-Leveling.jpg"),
                author = "Chugong, DUBU",
                genres = listOf("Action", "Adventure", "Fantasy", "Manhwa"),
                status = "Completed",
                rating = 8.6,
                chapterCount = 179
            ),
            Comic(
                id = "kimetsu-no-yaiba",
                title = "Kimetsu no Yaiba",
                description = "Petualangan Tanjiro Kamado menjadi pemburu iblis demi menyembuhkan adiknya Nezuko yang diubah menjadi iblis.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Kimetsu-no-Yaiba.png"),
                author = "Gotouge Koyoharu",
                genres = listOf("Action", "Adventure", "Drama", "Manga"),
                status = "Completed",
                rating = 8.1,
                chapterCount = 205
            ),
            Comic(
                id = "shingeki-no-kyojin",
                title = "Shingeki no Kyojin",
                description = "Perjuangan umat manusia yang terkurung di dalam dinding dari kepungan para raksasa pemakan manusia (Titan).",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Shingeki-no-Kyojin.png"),
                author = "Isayama Hajime",
                genres = listOf("Action", "Drama", "Fantasy", "Mystery"),
                status = "Completed",
                rating = 8.4,
                chapterCount = 139
            ),
            Comic(
                id = "naruto",
                title = "Naruto",
                description = "Kisah perjalanan Naruto Uzumaki, ninja periang yang bercita-cita menjadi Hokage di desa Konohagakure.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Naruto.png"),
                author = "Kishimoto Masashi",
                genres = listOf("Action", "Adventure", "Comedy", "Shounen"),
                status = "Completed",
                rating = 8.0,
                chapterCount = 700
            ),
            Comic(
                id = "bleach",
                title = "Bleach",
                description = "Ichigo Kurosaki memperoleh kekuatan Shinigami untuk melindungi orang-orang yang dicintainya dari ancaman Hollow.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Bleach.jpg"),
                author = "Kubo Tite",
                genres = listOf("Action", "Adventure", "Comedy", "Supernatural"),
                status = "Completed",
                rating = 7.6,
                chapterCount = 686
            ),
            Comic(
                id = "death-note",
                title = "Death Note",
                description = "Pertarungan kecerdasan antara Light Yagami pemegang buku kematian dan detektif jenius legendaris L.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Death-Note.jpg"),
                author = "Ohba Tsugumi, Obata Takeshi",
                genres = listOf("Drama", "Mystery", "Psychological", "Thriller"),
                status = "Completed",
                rating = 8.6,
                chapterCount = 108
            ),
            Comic(
                id = "tokyo-ghoul",
                title = "Tokyo Ghoul",
                description = "Ken Kaneki berubah menjadi setengah ghoul setelah kencan tragis dan harus berjuang bertahan di dunia gelap Tokyo.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Toukyou-Ghoul.jpg"),
                author = "Ishida Sui",
                genres = listOf("Action", "Drama", "Horror", "Psychological"),
                status = "Completed",
                rating = 8.38,
                chapterCount = 143
            ),
            Comic(
                id = "gintama",
                title = "Gintama",
                description = "Kisah komedi aksi Gintoki Sakata, seorang samurai nyentrik di era Edo yang diinvasi oleh alien Amanto.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Gintama.png"),
                author = "Sorachi Hideaki",
                genres = listOf("Action", "Adventure", "Comedy", "Sci-Fi"),
                status = "Completed",
                rating = 8.7,
                chapterCount = 704
            ),
            Comic(
                id = "dr-stone",
                title = "Dr. Stone",
                description = "Senku Ishigami berusaha membangkitkan kembali peradaban manusia dari zaman batu menggunakan kekuatan sains.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Dr-Stone.jpg"),
                author = "Inagaki Riichiro, Boichi",
                genres = listOf("Action", "Adventure", "Comedy", "Sci-Fi"),
                status = "Completed",
                rating = 7.4,
                chapterCount = 232
            ),
            Comic(
                id = "jigokuraku",
                title = "Jigokuraku",
                description = "Gabimaru the Hollow dikirim ke pulau misterius berbahaya untuk mencari ramuan keabadian demi pengampunan.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/11/Komik-Jigokuraku-KAKU-Yuuji.jpg"),
                author = "Kaku Yuuji",
                genres = listOf("Action", "Adventure", "Drama", "Historical"),
                status = "Completed",
                rating = 8.1,
                chapterCount = 127
            ),
            Comic(
                id = "fairy-tail",
                title = "Fairy Tail",
                description = "Petualangan Natsu Dragneel bersama guild penyihir terkuat Fairy Tail menjelajahi kerajaan Fiore.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Fairy-Tail.png"),
                author = "Mashima Hiro",
                genres = listOf("Action", "Adventure", "Comedy", "Magic"),
                status = "Completed",
                rating = 7.9,
                chapterCount = 545
            ),
            Comic(
                id = "shokugeki-no-soma",
                title = "Shokugeki no Soma",
                description = "Yukihira Soma bertekad menjadi koki terbaik di Akademi Kuliner Totsuki yang penuh persaingan ketat.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Shokugeki-no-Soma.png"),
                author = "Tsukuda Yuuto, Saeki Shun",
                genres = listOf("Comedy", "Drama", "Ecchi", "School Life"),
                status = "Completed",
                rating = 8.2,
                chapterCount = 315
            ),
            Comic(
                id = "akame-ga-kill",
                title = "Akame ga KILL!",
                description = "Tatsumi bergabung dengan kelompok pembunuh bayaran Night Raid untuk menggulingkan kekaisaran yang korup.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Akame-ga-Kiru.png"),
                author = "Takahiro, Tashiro Tetsuya",
                genres = listOf("Action", "Drama", "Fantasy", "Tragedy"),
                status = "Completed",
                rating = 8.0,
                chapterCount = 78
            ),
            Comic(
                id = "horimiya",
                title = "Horimiya",
                description = "Kisah romansa manis antara Hori dan Miyamura yang saling berbagi kepribadian rahasia di luar sekolah.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/11/Komik-Horimiya.png"),
                author = "HERO, Hagiwara Daisuke",
                genres = listOf("Comedy", "Romance", "School Life", "Slice of Life"),
                status = "Completed",
                rating = 8.7,
                chapterCount = 125
            ),
            Comic(
                id = "nisekoi",
                title = "Nisekoi",
                description = "Raku Ichijo dan Chitoge Kirisaki harus berpura-pura berpacaran demi mencegah perang antar keluarga geng.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/11/Komik-Nisekoi.jpg"),
                author = "Komi Naoshi",
                genres = listOf("Comedy", "Drama", "Harem", "Romance"),
                status = "Completed",
                rating = 7.4,
                chapterCount = 229
            ),
            Comic(
                id = "bakuman",
                title = "Bakuman",
                description = "Dua sahabat berjuang menembus industri manga Shonen Jump demi mewujudkan impian serialisasi dan adaptasi anime.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Bakuman.png"),
                author = "Ohba Tsugumi, Obata Takeshi",
                genres = listOf("Comedy", "Drama", "Romance", "Shounen"),
                status = "Completed",
                rating = 8.5,
                chapterCount = 176
            ),
            Comic(
                id = "beelzebub",
                title = "Beelzebub",
                description = "Oga Tatsumi murid SMA berandalan terkuat terpilih mengasuh bayi calon raja iblis yang dikirim ke bumi.",
                coverUrl = ComicMapper.sanitizeCoverUrl("https://i2.wp.com/bacakomik.my/wp-content/uploads/2023/10/Komik-Beelzebub.png"),
                author = "Tamura Ryuuhei",
                genres = listOf("Action", "Comedy", "Drama", "Supernatural"),
                status = "Completed",
                rating = 8.5,
                chapterCount = 240
            )
        )

        try {
            val response = sankaApi.getTopComics()
            val apiComics = response.komikList?.map {
                ComicMapper.mapSankaItemToComic(it).copy(status = "Completed")
            } ?: emptyList()
            val combined = (completedMasterpieces + apiComics).distinctBy { it.id }
            return@withContext Result.success(combined)
        } catch (_: Exception) {}

        Result.success(completedMasterpieces)
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
        val targetGenre = if (!category.isNullOrBlank() && !category.equals("All", ignoreCase = true)) category else null

        // 1. If query is blank and genre is selected: fetch from Sanka genre endpoint!
        if (query.isBlank() && targetGenre != null) {
            val genreSlug = targetGenre.lowercase().replace(" ", "-")
            try {
                val genreResp = sankaApi.getComicsByGenre(genreSlug)
                val comics = genreResp.komikList?.map { ComicMapper.mapSankaItemToComic(it, targetGenre) } ?: emptyList()
                if (comics.isNotEmpty()) {
                    return@withContext Result.success(comics)
                }
            } catch (_: Exception) {}
        }

        // 2. If query is not blank: search by query
        if (query.isNotBlank()) {
            try {
                val sankaResp = sankaApi.searchComics(query.trim())
                val sankaComics = sankaResp.komikList?.map { ComicMapper.mapSankaItemToComic(it, targetGenre) } ?: emptyList()
                if (sankaComics.isNotEmpty()) {
                    return@withContext Result.success(sankaComics)
                }
            } catch (_: Exception) {}
        }

        // 3. If both blank or "All"
        if (query.isBlank() && targetGenre == null) {
            return@withContext getPopularComics()
        }

        val localMatches = LegalDemoComicProvider.demoComics.filter { comic ->
            val matchQuery = query.isBlank() || comic.title.contains(query, ignoreCase = true) || comic.description.contains(query, ignoreCase = true)
            val matchGenre = targetGenre == null || comic.genres.any { it.contains(targetGenre, ignoreCase = true) }
            matchQuery && matchGenre
        }
        Result.success(localMatches)
    }

    override suspend fun getGenres(): List<String> = withContext(Dispatchers.IO) {
        val defaultList = listOf(
            "Action", "Adventure", "Comedy", "Crime", "Demons", "Drama", "Ecchi",
            "Fantasy", "Game", "Harem", "Historical", "Horror", "Isekai", "Josei",
            "Magic", "Martial Arts", "Mature", "Mecha", "Military", "Mystery",
            "Psychological", "Reincarnation", "Romance", "School Life", "Sci-Fi",
            "Seinen", "Shoujo", "Shounen", "Slice of Life", "Sports", "Superhero",
            "Supernatural", "Thriller", "Tragedy", "Wuxia"
        )
        try {
            val response = sankaApi.getGenres()
            val apiGenres = response.genres
                ?.mapNotNull { it.title?.trim() }
                ?.filter { title ->
                    title.length in 3..25 &&
                    !title.equals("Actio", ignoreCase = true) &&
                    !title.contains(".")
                }
                ?.map { it.split(" ").joinToString(" ") { word -> word.replaceFirstChar { c -> c.uppercase() } } }
                ?.distinct()
                ?: emptyList()

            if (apiGenres.isNotEmpty()) {
                val combined = (defaultList + apiGenres).distinct()
                return@withContext combined
            }
        } catch (_: Exception) {}

        defaultList
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

    override suspend fun removeFavorite(comicId: String) = withContext(Dispatchers.IO) {
        database.comicDao().updateFavorite(comicId, false)
        database.comicDao().deleteComicById(comicId)
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
