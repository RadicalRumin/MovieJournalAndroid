package com.example.moviejournal.data.local

import com.google.gson.annotations.SerializedName


data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    val adult : Boolean,
    @SerializedName("genre_ids") val genreIds: List<Int> = listOf(),
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title") val originalTitle : String,
    val popularity : Double,
    val video : Boolean,
) {
    fun fullPosterPath() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
}