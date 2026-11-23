package com.aziz.comicverse.data.remote.kitsu

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KitsuApiService {

    @GET("api/edge/trending/manga")
    suspend fun getTrendingManga(): KitsuMangaListResponse

    @GET("api/edge/manga")
    suspend fun getPopularManga(
        @Query("sort") sort: String = "-userCount",
        @Query("page[limit]") limit: Int = 20,
        @Query("page[offset]") offset: Int = 0
    ): KitsuMangaListResponse

    @GET("api/edge/manga")
    suspend fun getLatestManga(
        @Query("sort") sort: String = "-updatedAt",
        @Query("page[limit]") limit: Int = 20
    ): KitsuMangaListResponse

    @GET("api/edge/manga")
    suspend fun getCompletedManga(
        @Query("filter[status]") status: String = "finished",
        @Query("sort") sort: String = "-userCount",
        @Query("page[limit]") limit: Int = 20
    ): KitsuMangaListResponse

    @GET("api/edge/manga")
    suspend fun searchManga(
        @Query("filter[text]") query: String,
        @Query("filter[categories]") category: String? = null,
        @Query("page[limit]") limit: Int = 20
    ): KitsuMangaListResponse

    @GET("api/edge/manga/{id}")
    suspend fun getMangaById(
        @Path("id") id: String
    ): KitsuMangaSingleResponse
}
