package com.aziz.comicverse.data.mapper

import com.aziz.comicverse.data.remote.kitsu.KitsuMangaItem
import com.aziz.comicverse.data.remote.mangadex.MangaDexChapterItem
import com.aziz.comicverse.data.remote.mangadex.MangaDexItem
import com.aziz.comicverse.data.remote.sanka.SankaChapterItem
import com.aziz.comicverse.data.remote.sanka.SankaDetailData
import com.aziz.comicverse.data.remote.sanka.SankaKomikItem
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

    fun mapSankaItemToComic(item: SankaKomikItem): Comic {
        val slug = item.slug ?: item.title?.replace(" ", "-")?.lowercase() ?: "comic-${System.currentTimeMillis()}"
        val ratingDouble = item.rating?.toDoubleOrNull() ?: 4.5
        val comicType = item.type?.replaceFirstChar { it.uppercase() } ?: "Manga"
        val desc = buildString {
            append("Komik $comicType Indonesia.")
            if (!item.chapter.isNullOrBlank()) append(" ${item.chapter}.")
            if (!item.date.isNullOrBlank()) append(" (${item.date})")
        }

        return Comic(
            id = slug,
            title = item.title ?: "Untitled Comic",
            description = desc,
            coverUrl = item.cover ?: "",
            author = "BacaKomik Indo",
            genres = listOf(comicType, "Bahasa Indonesia"),
            status = "Ongoing",
            rating = ratingDouble,
            chapterCount = item.chapter?.filter { it.isDigit() }?.toIntOrNull(),
            isDemoContent = false
        )
    }

    fun mapSankaDetailToComic(slug: String, detail: SankaDetailData): Comic {
        val ratingDouble = detail.rating?.toDoubleOrNull() ?: 4.5
        val comicType = detail.type?.replaceFirstChar { it.uppercase() } ?: "Manga"
        val genres = detail.genres?.mapNotNull { it.title }?.filter { it.isNotBlank() }?.ifEmpty {
            listOf(comicType, "Bahasa Indonesia")
        } ?: listOf(comicType, "Bahasa Indonesia")

        val authorName = listOfNotNull(detail.author, detail.artist)
            .filter { it.isNotBlank() }
            .joinToString(", ")
            .ifBlank { "BacaKomik" }

        val desc = detail.synopsis?.ifBlank { null }
            ?: "Komik bahasa Indonesia bergenre ${genres.joinToString(", ")}."

        return Comic(
            id = slug,
            title = detail.title ?: slug.replace("-", " ").replaceFirstChar { it.uppercase() },
            description = desc,
            coverUrl = detail.cover ?: "",
            author = authorName,
            genres = genres,
            status = detail.status?.replaceFirstChar { it.uppercase() } ?: "Ongoing",
            rating = ratingDouble,
            chapterCount = detail.chapters?.size,
            isDemoContent = false
        )
    }

    fun mapSankaChapter(item: SankaChapterItem, comicSlug: String): ComicChapter {
        val slug = item.slug ?: ""
        val rawTitle = item.title?.trim()
        val titleText = if (!rawTitle.isNullOrBlank()) {
            rawTitle
        } else {
            val chMatch = Regex("""(?i)chapter[- ]*(\d+.*)""").find(slug)
            if (chMatch != null) {
                "Chapter ${chMatch.groupValues[1].replace("-", " ").trim()}"
            } else {
                slug.replace("-", " ").replaceFirstChar { it.uppercase() }
            }
        }
        val chNum = Regex("""(?i)chapter\s*(\d+(\.\d+)?)""").find(titleText)?.groupValues?.get(1)
            ?: Regex("""\d+""").find(titleText)?.value
            ?: "1"

        return ComicChapter(
            id = slug,
            comicId = comicSlug,
            title = "🇮🇩 $titleText",
            chapterNumber = chNum,
            releaseDate = item.date ?: "Terbaru",
            pageCount = 0
        )
    }
}
