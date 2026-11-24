package com.aziz.comicverse.data.remote.sanka

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SankaApiService {

    @GET("comic/bacakomik/populer")
    suspend fun getPopularComics(): SankaKomikListResponse

    @GET("comic/bacakomik/latest")
    suspend fun getLatestComics(@Query("page") page: Int = 1): SankaKomikListResponse

    @GET("comic/bacakomik/top")
    suspend fun getTopComics(): SankaKomikListResponse

    @GET("comic/bacakomik/search/{query}")
    suspend fun searchComics(@Path("query") query: String): SankaKomikListResponse

    @GET("comic/bacakomik/genre/{genre}")
    suspend fun getComicsByGenre(@Path("genre") genre: String): SankaKomikListResponse

    @GET("comic/bacakomik/detail/{slug}")
    suspend fun getComicDetail(@Path("slug") slug: String): SankaDetailResponse

    @GET("comic/bacakomik/chapter/{chapterSlug}")
    suspend fun getChapterDetail(@Path("chapterSlug") chapterSlug: String): SankaChapterResponse
}
