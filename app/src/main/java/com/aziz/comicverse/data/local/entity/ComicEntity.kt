package com.aziz.comicverse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comics")
data class ComicEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val author: String,
    val genres: String, // Comma separated
    val status: String,
    val rating: Double?,
    val isFavorite: Boolean = false,
    val isDemoContent: Boolean = false
)
