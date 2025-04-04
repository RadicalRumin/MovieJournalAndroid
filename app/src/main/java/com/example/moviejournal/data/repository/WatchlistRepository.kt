package com.example.moviejournal.data.repository

import android.content.Context
import com.example.moviejournal.data.local.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class WatchlistRepository(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("watchlist", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addToWatchlist(movie: Movie) {
        val watchlist = getWatchlist().toMutableList()
        val existingIndex = watchlist.indexOfFirst { it.id == movie.id }

        if (existingIndex >= 0) {
            watchlist[existingIndex] = movie.copy(
                rating = movie.rating ?: watchlist[existingIndex].rating,
                notes = movie.notes ?: watchlist[existingIndex].notes
            )
        } else {
            // Add new movie
            watchlist.add(movie)
        }
        saveWatchlist(watchlist)
    }

    fun updateMovieReview(movieId: Int, rating: Int, notes: String) {
        val watchlist = getWatchlist().toMutableList()
        val index = watchlist.indexOfFirst { it.id == movieId }
        if (index >= 0) {
            watchlist[index] = watchlist[index].copy(rating = rating, notes = notes)
            saveWatchlist(watchlist)
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        val watchlist = getWatchlist().filter { it.id != movieId }
        saveWatchlist(watchlist)
    }

    fun getWatchlist(): List<Movie> {
        val json = sharedPrefs.getString("movies", "[]") ?: "[]"
        val type = object : TypeToken<List<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isOnWatchlist(movieId: Int): Boolean {
        return getWatchlist().any { it.id == movieId }
    }

    fun getMovie(movieId: Int): Movie? {
        return getWatchlist().firstOrNull { it.id == movieId }
    }

    private fun saveWatchlist(movies: List<Movie>) {
        sharedPrefs.edit {
            putString("movies", gson.toJson(movies))
        }
    }
}