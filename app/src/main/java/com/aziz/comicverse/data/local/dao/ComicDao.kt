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

    @Query("SELECT * FROM comics")
    suspend fun getAllComicsRaw(): List<ComicEntity>

    @Query("DELETE FROM comics WHERE id = :comicId")
    suspend fun deleteComicById(comicId: String): Int

    @Query("DELETE FROM comics WHERE id = '37280' OR title LIKE '%Kimetsu%' OR title LIKE '%Yaiba%'")
    suspend fun purgeBrokenKnyComics(): Int
}
