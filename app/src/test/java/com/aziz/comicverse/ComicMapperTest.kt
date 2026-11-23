package com.aziz.comicverse

import com.aziz.comicverse.data.mapper.ComicMapper
import com.aziz.comicverse.data.remote.kitsu.KitsuMangaAttributes
import com.aziz.comicverse.data.remote.kitsu.KitsuMangaItem
import com.aziz.comicverse.data.remote.kitsu.KitsuPosterImage
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

        val comic = ComicMapper.mapKitsuItemToComic(item)

        assertEquals("42", comic.id)
        assertEquals("One Piece", comic.title)
        assertEquals("A legendary pirate story.", comic.description)
        assertEquals("large.jpg", comic.coverUrl)
        assertEquals("Weekly Shonen Jump", comic.author)
        assertEquals(4.25, comic.rating ?: 0.0, 0.01)
        assertEquals("Current", comic.status)
        assertNotNull(comic.genres)
    }
}
