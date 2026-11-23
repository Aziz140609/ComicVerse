package com.aziz.comicverse

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
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
            .crossfade(true)
            .build()
    }

    companion object {
        lateinit var instance: ComicVerseApp
            private set
    }
}
