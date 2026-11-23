package com.aziz.comicverse

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.aziz.comicverse.data.local.ComicDatabase
import com.aziz.comicverse.data.remote.NetworkClient

class ComicVerseApp : Application(), ImageLoaderFactory {

    lateinit var database: ComicDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = ComicDatabase.getInstance(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(NetworkClient.okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.10)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    companion object {
        lateinit var instance: ComicVerseApp
            private set
    }
}
