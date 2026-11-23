package com.aziz.comicverse.data.mapper

import com.aziz.comicverse.data.remote.kitsu.KitsuMangaItem
import com.aziz.comicverse.data.remote.mangadex.MangaDexChapterItem
import com.aziz.comicverse.data.remote.mangadex.MangaDexItem
import com.aziz.comicverse.domain.model.Comic
import com.aziz.comicverse.domain.model.ComicChapter

object ComicMapper {

    fun mapMangaDexItemToComic(item: MangaDexItem): Comic {
        val attr = item.attributes

        val title = attr?.title?.values?.firstOrNull() ?: "Untitled Manga"
        val description = attr?.description?.get("en")
            ?: attr?.description?.get("id")
            ?: attr?.description?.values?.firstOrNull()
            ?: "Tidak ada deskripsi tersedia."

        val coverRel = item.relationships?.find { it.type == "cover_art" }
        val coverFileName = coverRel?.attributes?.fileName
        val coverUrl = if (!coverFileName.isNullOrBlank()) {
            "https://uploads.mangadex.org/covers/${item.id}/$coverFileName.256.jpg"
        } else {
            ""
        }

        val authorRel = item.relationships?.find { it.type == "author" || it.type == "artist" }
        val authorName = authorRel?.attributes?.name ?: "MangaDex Artist"

        val genres = attr?.tags?.mapNotNull { tag ->
            tag.attributes?.name?.get("en")
        }?.filter { it.isNotBlank() } ?: listOf("Manga")

        val status = attr?.status?.replaceFirstChar { it.uppercase() } ?: "Ongoing"

        return Comic(
            id = item.id,
            title = title,
            description = description,
            coverUrl = coverUrl,
            author = authorName,
            genres = if (genres.isEmpty()) listOf("Manga", "Action") else genres,
            status = status,
            rating = 4.8,
            chapterCount = attr?.lastChapter?.toIntOrNull(),
            isDemoContent = false
        )
    }

    fun mapMangaDexChapter(item: MangaDexChapterItem, mangaId: String): ComicChapter {
        val attr = item.attributes
        val chNum = attr?.chapter ?: "1"
        val lang = attr?.translatedLanguage ?: "id"
        val langPrefix = if (lang == "id") "[ID] " else if (lang == "en") "[EN] " else ""
        val titleText = if (!attr?.title.isNullOrBlank()) {
            "${langPrefix}Chapter $chNum: ${attr.title}"
        } else {
            "${langPrefix}Chapter $chNum"
        }

        val langBadge = if (lang == "id") "🇮🇩 Indo" else "🇬🇧 Eng"
        val dateText = attr?.publishAt?.take(10) ?: "Terbaru"

        return ComicChapter(
            id = item.id,
            comicId = mangaId,
            title = titleText,
            chapterNumber = chNum,
            releaseDate = "$langBadge • $dateText",
            pageCount = attr?.pages ?: 0
        )
    }

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

        val ratingDouble = attr?.averageRating?.toDoubleOrNull()?.let { it / 20.0 }

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
