package com.aziz.comicverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aziz.comicverse.data.local.entity.ComicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(comic: ComicEntity)

    @Query("UPDATE comics SET isFavorite = :isFavorite WHERE id = :comicId")
    suspend fun updateFavorite(comicId: String, isFavorite: Boolean): Int

    @Query("SELECT * FROM comics WHERE isFavorite = 1")
    fun getFavoriteComics(): Flow<List<ComicEntity>>

    @Query("SELECT isFavorite FROM comics WHERE id = :comicId")
    fun isFavorite(comicId: String): Flow<Boolean?>

    @Query("SELECT * FROM comics WHERE id = :comicId LIMIT 1")
    suspend fun getComicById(comicId: String): ComicEntity?
}
