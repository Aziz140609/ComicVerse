package com.aziz.comicverse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aziz.comicverse.data.local.dao.ComicDao
import com.aziz.comicverse.data.local.dao.ReadingProgressDao
import com.aziz.comicverse.data.local.entity.ComicEntity
import com.aziz.comicverse.data.local.entity.ReadingProgressEntity

@Database(
    entities = [ComicEntity::class, ReadingProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ComicDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun readingProgressDao(): ReadingProgressDao

    companion object {
        @Volatile
        private var INSTANCE: ComicDatabase? = null

        fun getInstance(context: Context): ComicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComicDatabase::class.java,
                    "comicverse_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
