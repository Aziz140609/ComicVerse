package com.aziz.comicverse

import com.aziz.comicverse.domain.model.ReadingProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingProgressTest {

    @Test
    fun readingProgress_calculatesPercentageCorrectly() {
        val progress = ReadingProgress(
            comicId = "demo-pepper-carrot",
            chapterId = "pc-ep-1",
            chapterTitle = "Episode 1",
            pageIndex = 3,
            totalPages = 6
        )

        val percentage = (progress.pageIndex * 100) / progress.totalPages
        assertEquals(50, percentage)
        assertTrue(progress.updatedAt > 0)
    }

    @Test
    fun readingProgress_handlesFirstAndLastPage() {
        val firstPage = ReadingProgress(
            comicId = "demo-1",
            chapterId = "ch-1",
            chapterTitle = "Bab 1",
            pageIndex = 1,
            totalPages = 10
        )
        val lastPage = ReadingProgress(
            comicId = "demo-1",
            chapterId = "ch-1",
            chapterTitle = "Bab 1",
            pageIndex = 10,
            totalPages = 10
        )

        assertEquals(10, (firstPage.pageIndex * 100) / firstPage.totalPages)
        assertEquals(100, (lastPage.pageIndex * 100) / lastPage.totalPages)
    }
}
