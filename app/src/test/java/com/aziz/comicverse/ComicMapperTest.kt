package com.aziz.comicverse

import com.aziz.comicverse.data.mapper.ComicMapper
import com.aziz.comicverse.data.remote.kitsu.KitsuMangaAttributes
import com.aziz.comicverse.data.remote.kitsu.KitsuMangaItem
import com.aziz.comicverse.data.remote.kitsu.KitsuPosterImage
import com.aziz.comicverse.data.remote.mangadex.MangaDexAttributes
import com.aziz.comicverse.data.remote.mangadex.MangaDexChapterAttributes
import com.aziz.comicverse.data.remote.mangadex.MangaDexChapterItem
import com.aziz.comicverse.data.remote.mangadex.MangaDexItem
import com.aziz.comicverse.data.remote.mangadex.MangaDexRelationship
import com.aziz.comicverse.data.remote.mangadex.MangaDexRelationshipAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ComicMapperTest {

    @Test
    fun mapKitsuItemToComic_extractsCorrectFields() {
        val item = KitsuMangaItem(
            id = "42",
            type = "manga",
            attributes = KitsuMangaAttributes(
                slug = "one-piece",
                synopsis = "A legendary pirate story.",
                description = "Full description here.",
                canonicalTitle = "One Piece",
                titles = mapOf("en" to "One Piece"),
                averageRating = "85.0",
                status = "current",
                posterImage = KitsuPosterImage(
                    tiny = "tiny.jpg",
                    small = "small.jpg",
                    medium = "medium.jpg",
                    large = "large.jpg",
                    original = "orig.jpg"
                ),
                chapterCount = 1100,
                volumeCount = 108,
                subtype = "manga",
                serialization = "Weekly Shonen Jump"
            )
        )

        val kitsuComic = ComicMapper.mapKitsuItemToComic(item)
        assertEquals("42", kitsuComic.id)
        assertEquals("One Piece", kitsuComic.title)
        assertEquals("A legendary pirate story.", kitsuComic.description)
        assertEquals("large.jpg", kitsuComic.coverUrl)
        assertEquals("Weekly Shonen Jump", kitsuComic.author)
        assertEquals(4.25, kitsuComic.rating ?: 0.0, 0.01)
        assertEquals("Current", kitsuComic.status)
        assertNotNull(kitsuComic.genres)
    }

    @Test
    fun mapMangaDexItemToComic_extractsCorrectFields() {
        val mangaItem = MangaDexItem(
            id = "md-123",
            type = "manga",
            attributes = MangaDexAttributes(
                title = mapOf("en" to "Frieren: Beyond Journey's End"),
                description = mapOf("en" to "The adventure is over but life goes on."),
                status = "ongoing",
                contentRating = "safe",
                tags = emptyList(),
                lastChapter = "130"
            ),
            relationships = listOf(
                MangaDexRelationship(
                    id = "rel-cover",
                    type = "cover_art",
                    attributes = MangaDexRelationshipAttributes(fileName = "frieren.jpg", name = null)
                ),
                MangaDexRelationship(
                    id = "rel-author",
                    type = "author",
                    attributes = MangaDexRelationshipAttributes(fileName = null, name = "Kanehito Yamada")
                )
            )
        )

        val comic = ComicMapper.mapMangaDexItemToComic(mangaItem)

        assertEquals("md-123", comic.id)
        assertEquals("Frieren: Beyond Journey's End", comic.title)
        assertEquals("Kanehito Yamada", comic.author)
        assertEquals("https://uploads.mangadex.org/covers/md-123/frieren.jpg.256.jpg", comic.coverUrl)
        assertEquals("Ongoing", comic.status)
    }

    @Test
    fun mapMangaDexChapter_parsesChapterTitleAndNumber() {
        val chapterItem = MangaDexChapterItem(
            id = "ch-1",
            type = "chapter",
            attributes = MangaDexChapterAttributes(
                volume = "1",
                chapter = "1",
                title = "The End of the Journey",
                pages = 50,
                publishAt = null,
                readableAt = null,
                translatedLanguage = "en",
                externalUrl = null
            )
        )

        val chapter = ComicMapper.mapMangaDexChapter(chapterItem, "md-123")
        assertEquals("ch-1", chapter.id)
        assertEquals("md-123", chapter.comicId)
        assertEquals("[EN] Chapter 1: The End of the Journey", chapter.title)
        assertEquals("1", chapter.chapterNumber)
    }
}
