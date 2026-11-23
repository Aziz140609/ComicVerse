package com.aziz.comicverse.data.remote.kitsu

import com.google.gson.annotations.SerializedName

data class KitsuMangaListResponse(
    @SerializedName("data") val data: List<KitsuMangaItem>?,
    @SerializedName("meta") val meta: KitsuMeta?
)

data class KitsuMangaSingleResponse(
    @SerializedName("data") val data: KitsuMangaItem?
)

data class KitsuMangaItem(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String?,
    @SerializedName("attributes") val attributes: KitsuMangaAttributes?
)

data class KitsuMangaAttributes(
    @SerializedName("slug") val slug: String?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("canonicalTitle") val canonicalTitle: String?,
    @SerializedName("titles") val titles: Map<String, String>?,
    @SerializedName("averageRating") val averageRating: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("posterImage") val posterImage: KitsuPosterImage?,
    @SerializedName("chapterCount") val chapterCount: Int?,
    @SerializedName("volumeCount") val volumeCount: Int?,
    @SerializedName("subtype") val subtype: String?,
    @SerializedName("serialization") val serialization: String?
)

data class KitsuPosterImage(
    @SerializedName("tiny") val tiny: String?,
    @SerializedName("small") val small: String?,
    @SerializedName("medium") val medium: String?,
    @SerializedName("large") val large: String?,
    @SerializedName("original") val original: String?
)

data class KitsuMeta(
    @SerializedName("count") val count: Int?
)
