package com.example.moviejournal.data.local


data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
//    @SerializedName("poster_path") val posterPath: String?,
//    @SerializedName("release_date") val releaseDate: String,
//    @SerializedName("vote_average") val voteAverage: Double,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val adult: Boolean = false,
    val backdropPath: String? = null
) {
    fun fullPosterPath() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
}