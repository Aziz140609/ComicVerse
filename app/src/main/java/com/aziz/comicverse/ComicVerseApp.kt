package com.aziz.comicverse

import android.app.Application
import com.aziz.comicverse.data.local.ComicDatabase

class ComicVerseApp : Application() {

    lateinit var database: ComicDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = ComicDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: ComicVerseApp
            private set
    }
}
