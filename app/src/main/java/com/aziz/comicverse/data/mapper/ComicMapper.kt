package com.aziz.comicverse.data.mapper

import com.aziz.comicverse.data.remote.kitsu.KitsuMangaItem
import com.aziz.comicverse.domain.model.Comic

object ComicMapper {

    fun mapKitsuItemToComic(item: KitsuMangaItem): Comic {
        val attr = item.attributes

        val title = attr?.canonicalTitle
            ?: attr?.titles?.get("en")
            ?: attr?.titles?.get("en_jp")
            ?: "Untitled Comic"

        val description = attr?.synopsis
            ?: attr?.description
            ?: "Tidak ada deskripsi tersedia."

        val coverUrl = attr?.posterImage?.large
            ?: attr?.posterImage?.medium
            ?: attr?.posterImage?.original
            ?: attr?.posterImage?.small
            ?: ""

        val ratingDouble = attr?.averageRating?.toDoubleOrNull()?.let { it / 20.0 } // Scale 0..100 to 0..5

        val subtype = attr?.subtype?.replaceFirstChar { it.uppercase() } ?: "Manga"
        val genres = listOfNotNull(subtype, attr?.serialization).filter { it.isNotBlank() }

        return Comic(
            id = item.id,
            title = title,
            description = description,
            coverUrl = coverUrl,
            author = attr?.serialization ?: "Unknown Author",
            genres = if (genres.isEmpty()) listOf("Manga", "Action") else genres,
            status = attr?.status?.replaceFirstChar { it.uppercase() } ?: "Ongoing",
            rating = ratingDouble,
            chapterCount = attr?.chapterCount,
            isDemoContent = false
        )
    }
}
