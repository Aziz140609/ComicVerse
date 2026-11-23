package com.aziz.comicverse.data.remote.mangadex

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaDexApiService {

    @GET("manga")
    suspend fun getPopularManga(
        @Query("limit") limit: Int = 20,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author"),
        @Query("order[followedCount]") order: String = "desc",
        @Query("contentRating[]") rating: List<String> = listOf("safe", "suggestive"),
        @Query("hasAvailableChapters") hasChapters: String = "true"
    ): MangaDexListResponse

    @GET("manga")
    suspend fun searchManga(
        @Query("title") title: String,
        @Query("limit") limit: Int = 20,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author"),
        @Query("contentRating[]") rating: List<String> = listOf("safe", "suggestive")
    ): MangaDexListResponse

    @GET("manga/{id}")
    suspend fun getMangaById(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author")
    ): MangaDexSingleResponse

    @GET("manga/{id}/feed")
    suspend fun getMangaFeed(
        @Path("id") mangaId: String,
        @Query("order[chapter]") order: String = "asc",
        @Query("limit") limit: Int = 96,
        @Query("contentRating[]") rating: List<String> = listOf("safe", "suggestive")
    ): MangaDexFeedResponse

    @GET("at-home/server/{chapterId}")
    suspend fun getAtHomeServer(
        @Path("chapterId") chapterId: String
    ): MangaDexAtHomeResponse
}
