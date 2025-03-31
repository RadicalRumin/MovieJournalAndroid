package com.example.moviejournal.data.database

// data/local/entity/MovieEntity.kt
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    // Additional fields for local storage
    val isWatched: Boolean = false,
    val userRating: Int = 0
)