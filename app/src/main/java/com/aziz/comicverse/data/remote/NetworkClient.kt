package com.aziz.comicverse.data.remote

import com.aziz.comicverse.data.remote.kitsu.KitsuApiService
import com.aziz.comicverse.data.remote.mangadex.MangaDexApiService
import com.aziz.comicverse.data.remote.mangadex.MangaDexDns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val MANGADEX_BASE_URL = "https://api.mangadex.org/"
    private const val KITSU_BASE_URL = "https://kitsu.io/"

    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("User-Agent", "ComicVerse/1.0 (Android; Mobile Programming Project)")

        val host = original.url.host
        if (host.contains("mangadex.network") || host.contains("mangadex.org")) {
            requestBuilder.header("Referer", "https://mangadex.org/")
        }
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    // Fast-fail client for MangaDex since it is blocked by Indonesian ISPs (Trust Positif / Internet Baik)
    val mangaDexClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(MangaDexDns())
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectionPool(okhttp3.ConnectionPool(10, 5, TimeUnit.MINUTES))
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .build()
    }

    // Stable, unblocked client for Kitsu API and Image loading
    val kitsuClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectionPool(okhttp3.ConnectionPool(10, 5, TimeUnit.MINUTES))
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    // Default client for Coil image loading and general requests
    val okHttpClient: OkHttpClient get() = kitsuClient

    val mangaDexApi: MangaDexApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MANGADEX_BASE_URL)
            .client(mangaDexClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MangaDexApiService::class.java)
    }

    val kitsuApi: KitsuApiService by lazy {
        Retrofit.Builder()
            .baseUrl(KITSU_BASE_URL)
            .client(kitsuClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KitsuApiService::class.java)
    }
}
