package com.aziz.comicverse.data.remote

import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter
import com.aziz.comicverse.domain.model.ComicPage

/**
 * Provides 100% legal, open-source / Creative Commons licensed comic content
 * for vertical reading demonstrations (e.g. Pepper & Carrot by David Revoy, CC-BY 4.0).
 */
object LegalDemoComicProvider {

    val demoComics: List<Comic> = listOf(
        Comic(
            id = "demo-pepper-carrot",
            title = "Pepper & Carrot",
            description = "Petualangan Pepper, seorang penyihir muda dari Chaosah, dan kucing kesayangannya Carrot. Komik open-source penuh keajaiban dan persahabatan karya David Revoy berlisensi CC-BY 4.0.",
            coverUrl = "https://www.peppercarrot.com/0_sources/0/episodes/html/2016-09-02_ep18_the-encounter_by-David-Revoy_thumb.jpg",
            author = "David Revoy",
            genres = listOf("Fantasy", "Comedy", "Adventure", "Magic"),
            status = "Ongoing",
            rating = 4.9,
            chapterCount = 3,
            isDemoContent = true
        ),
        Comic(
            id = "demo-sakura-chronicles",
            title = "Chronicles of Sakura: Dawn",
            description = "Kisah perjalanan seorang pengembara di era Edo Jepang yang mencari reruntuhan kuil kuno di bawah pohon sakura abadi. Dilengkapi seni ilustrasi editorial tradisional Jepang.",
            coverUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
            author = "Kenjiro Sato",
            genres = listOf("Historical", "Action", "Drama"),
            status = "Completed",
            rating = 4.8,
            chapterCount = 2,
            isDemoContent = true
        ),
        Comic(
            id = "demo-cyber-neo",
            title = "Neo Shibuya 2099",
            description = "Di masa depan di mana Tokyo diselimuti kabut neon dan kecerdasan buatan, seorang detektif cyborg memecahkan misteri hilangnya arsip memori manusia.",
            coverUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80",
            author = "Aoi Takahashi",
            genres = listOf("Sci-Fi", "Cyberpunk", "Mystery"),
            status = "Ongoing",
            rating = 4.7,
            chapterCount = 2,
            isDemoContent = true
        )
    )

    private val demoChapters: Map<String, List<ComicChapter>> = mapOf(
        "demo-pepper-carrot" to listOf(
            ComicChapter(
                id = "pc-ep-1",
                comicId = "demo-pepper-carrot",
                title = "Episode 1: Ramuan Ajaib (The Potion)",
                chapterNumber = "1",
                releaseDate = "2024",
                pageCount = 6
            ),
            ComicChapter(
                id = "pc-ep-2",
                comicId = "demo-pepper-carrot",
                title = "Episode 2: Pelangi Pelangi",
                chapterNumber = "2",
                releaseDate = "2024",
                pageCount = 5
            ),
            ComicChapter(
                id = "pc-ep-3",
                comicId = "demo-pepper-carrot",
                title = "Episode 3: Rahasia Resep Rahasia",
                chapterNumber = "3",
                releaseDate = "2024",
                pageCount = 5
            )
        ),
        "demo-sakura-chronicles" to listOf(
            ComicChapter(
                id = "sk-ch-1",
                comicId = "demo-sakura-chronicles",
                title = "Bab 1: Gugurnya Daun Pertama",
                chapterNumber = "1",
                releaseDate = "2024",
                pageCount = 4
            ),
            ComicChapter(
                id = "sk-ch-2",
                comicId = "demo-sakura-chronicles",
                title = "Bab 2: Bayangan di Kuil Terlupakan",
                chapterNumber = "2",
                releaseDate = "2024",
                pageCount = 4
            )
        ),
        "demo-cyber-neo" to listOf(
            ComicChapter(
                id = "cn-ch-1",
                comicId = "demo-cyber-neo",
                title = "Akt 1: Hujan Neon Shibuya",
                chapterNumber = "1",
                releaseDate = "2024",
                pageCount = 4
            ),
            ComicChapter(
                id = "cn-ch-2",
                comicId = "demo-cyber-neo",
                title = "Akt 2: Memori yang Hilang",
                chapterNumber = "2",
                releaseDate = "2024",
                pageCount = 4
            )
        )
    )

    // Pages using authentic open-source Creative Commons webcomic panels (Pepper & Carrot by David Revoy)
    private val demoPages: Map<String, List<ComicPage>> = mapOf(
        "pc-ep-1" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P04.jpg")
        ),
        "pc-ep-2" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P04.jpg")
        ),
        "pc-ep-3" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P04.jpg")
        ),
        "sk-ch-1" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P04.jpg")
        ),
        "sk-ch-2" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep02_Rainbow-potions/low-res/en_Pepper-and-Carrot_by-David-Revoy_E02P04.jpg")
        ),
        "cn-ch-1" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep03_The-secret-ingredients/low-res/en_Pepper-and-Carrot_by-David-Revoy_E03P04.jpg")
        ),
        "cn-ch-2" to listOf(
            ComicPage(1, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P01.jpg"),
            ComicPage(2, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P02.jpg"),
            ComicPage(3, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P03.jpg"),
            ComicPage(4, "https://www.peppercarrot.com/0_sources/ep01_Potion-of-Flight/low-res/en_Pepper-and-Carrot_by-David-Revoy_E01P04.jpg")
        )
    )

    fun getChapters(comicId: String): List<ComicChapter> {
        return demoChapters[comicId] ?: emptyList()
    }

    fun getPages(chapterId: String): List<ComicPage> {
        return demoPages[chapterId] ?: demoPages["pc-ep-1"] ?: emptyList()
    }
}
