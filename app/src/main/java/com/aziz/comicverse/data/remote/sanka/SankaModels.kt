package com.aziz.comicverse.data.remote.sanka

import com.google.gson.annotations.SerializedName

data class SankaKomikListResponse(
    @SerializedName("creator") val creator: String? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("komikList") val komikList: List<SankaKomikItem>? = null,
    @SerializedName("hasNextPage") val hasNextPage: Boolean? = null,
    @SerializedName("currentPage") val currentPage: Int? = null
)

data class SankaKomikItem(
    @SerializedName("title") val title: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("chapter") val chapter: String? = null,
    @SerializedName("date") val date: String? = null
)

data class SankaDetailResponse(
    @SerializedName("creator") val creator: String? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("detail") val detail: SankaDetailData? = null
)

data class SankaDetailData(
    @SerializedName("title") val title: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("otherTitle") val otherTitle: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("artist") val artist: String? = null,
    @SerializedName("release") val release: String? = null,
    @SerializedName("series") val series: String? = null,
    @SerializedName("reader") val reader: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("chapters") val chapters: List<SankaChapterItem>? = null
)

data class SankaChapterItem(
    @SerializedName("title") val title: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("date") val date: String? = null
)

data class SankaChapterResponse(
    @SerializedName("creator") val creator: String? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("navigation") val navigation: SankaNavigation? = null
)

data class SankaNavigation(
    @SerializedName("next") val next: String? = null,
    @SerializedName("prev") val prev: String? = null
)
