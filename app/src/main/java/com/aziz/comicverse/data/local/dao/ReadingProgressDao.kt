package com.aziz.comicverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aziz.comicverse.data.local.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: ReadingProgressEntity)

    @Query("SELECT * FROM reading_progress WHERE comicId = :comicId LIMIT 1")
    fun getProgressByComicId(comicId: String): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress ORDER BY updatedAt DESC LIMIT 10")
    fun getAllProgress(): Flow<List<ReadingProgressEntity>>

    @Query("DELETE FROM reading_progress WHERE comicId NOT IN (SELECT comicId FROM reading_progress ORDER BY updatedAt DESC LIMIT 10)")
    suspend fun trimOldProgress()

    @Query("DELETE FROM reading_progress WHERE comicId = :comicId")
    suspend fun deleteProgress(comicId: String): Int

    @Query("DELETE FROM reading_progress WHERE comicId = '37280'")
    suspend fun purgeBrokenKnyProgress(): Int
}
