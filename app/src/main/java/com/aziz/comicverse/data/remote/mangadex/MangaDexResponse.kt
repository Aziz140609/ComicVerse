package com.aziz.comicverse.data.remote.mangadex

import com.google.gson.annotations.SerializedName

data class MangaDexListResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("data") val data: List<MangaDexItem>?,
    @SerializedName("total") val total: Int?
)

data class MangaDexSingleResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("data") val data: MangaDexItem?
)

data class MangaDexItem(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String?,
    @SerializedName("attributes") val attributes: MangaDexAttributes?,
    @SerializedName("relationships") val relationships: List<MangaDexRelationship>?
)

data class MangaDexAttributes(
    @SerializedName("title") val title: Map<String, String>?,
    @SerializedName("description") val description: Map<String, String>?,
    @SerializedName("status") val status: String?,
    @SerializedName("contentRating") val contentRating: String?,
    @SerializedName("tags") val tags: List<MangaDexTag>?,
    @SerializedName("lastChapter") val lastChapter: String?
)

data class MangaDexTag(
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: MangaDexTagAttributes?
)

data class MangaDexTagAttributes(
    @SerializedName("name") val name: Map<String, String>?
)

data class MangaDexRelationship(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String?,
    @SerializedName("attributes") val attributes: MangaDexRelationshipAttributes?
)

data class MangaDexRelationshipAttributes(
    @SerializedName("fileName") val fileName: String?,
    @SerializedName("name") val name: String?
)

// Chapters Feed
data class MangaDexFeedResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("data") val data: List<MangaDexChapterItem>?,
    @SerializedName("total") val total: Int?
)

data class MangaDexChapterItem(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String?,
    @SerializedName("attributes") val attributes: MangaDexChapterAttributes?
)

data class MangaDexChapterAttributes(
    @SerializedName("volume") val volume: String?,
    @SerializedName("chapter") val chapter: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("pages") val pages: Int?,
    @SerializedName("publishAt") val publishAt: String?,
    @SerializedName("readableAt") val readableAt: String?,
    @SerializedName("translatedLanguage") val translatedLanguage: String?,
    @SerializedName("externalUrl") val externalUrl: String?
)

// At-Home Server (Pages/Images)
data class MangaDexAtHomeResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("baseUrl") val baseUrl: String?,
    @SerializedName("chapter") val chapter: MangaDexAtHomeChapter?
)

data class MangaDexAtHomeChapter(
    @SerializedName("hash") val hash: String?,
    @SerializedName("data") val data: List<String>?,
    @SerializedName("dataSaver") val dataSaver: List<String>?
)
