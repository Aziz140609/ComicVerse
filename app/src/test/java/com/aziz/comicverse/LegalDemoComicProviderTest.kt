package com.aziz.comicverse

import com.aziz.comicverse.data.remote.LegalDemoComicProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LegalDemoComicProviderTest {

    @Test
    fun demoComics_areNotEmpty_andHaveValidMetadata() {
        val comics = LegalDemoComicProvider.demoComics
        assertTrue("Demo comics should not be empty", comics.isNotEmpty())

        comics.forEach { comic ->
            assertTrue("Comic ID should not be blank", comic.id.isNotBlank())
            assertTrue("Title should not be blank", comic.title.isNotBlank())
            assertTrue("Description should not be blank", comic.description.isNotBlank())
            assertTrue("Cover URL should not be blank", comic.coverUrl.isNotBlank())
            assertTrue("Author should not be blank", comic.author.isNotBlank())
            assertTrue("Genres should not be empty", comic.genres.isNotEmpty())
            assertTrue("isDemoContent flag should be true", comic.isDemoContent)
        }
    }

    @Test
    fun getChapters_returnsValidChapters_forDemoComic() {
        val pepperCarrotChapters = LegalDemoComicProvider.getChapters("demo-pepper-carrot")
        assertTrue("Pepper & Carrot chapters should exist", pepperCarrotChapters.isNotEmpty())

        val firstChapter = pepperCarrotChapters.first()
        assertTrue(firstChapter.title.contains("Episode 1", ignoreCase = true))
        assertTrue(firstChapter.pageCount > 0)
    }

    @Test
    fun getPages_returnsValidPages_forChapter() {
        val pages = LegalDemoComicProvider.getPages("pc-ep-1")
        assertFalse("Pages should not be empty", pages.isEmpty())

        pages.forEach { page ->
            assertTrue(page.index > 0)
            assertTrue(page.imageUrl.startsWith("http"))
        }
    }
}
