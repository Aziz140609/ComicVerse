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

    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val headerInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "ComicVerse/1.0 (Android; Mobile Programming Project)")

            val host = original.url.host
            if (host.contains("mangadex.network") || host.contains("mangadex.org")) {
                requestBuilder.header("Referer", "https://mangadex.org/")
            }
            chain.proceed(requestBuilder.build())
        }

        OkHttpClient.Builder()
            .dns(MangaDexDns())
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val mangaDexApi: MangaDexApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MANGADEX_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MangaDexApiService::class.java)
    }

    val kitsuApi: KitsuApiService by lazy {
        Retrofit.Builder()
            .baseUrl(KITSU_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KitsuApiService::class.java)
    }
}
