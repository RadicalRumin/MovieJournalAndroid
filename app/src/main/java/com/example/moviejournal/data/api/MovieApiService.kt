package com.example.moviejournal.data.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.moviejournal.BuildConfig
import com.example.moviejournal.data.local.Movie
import org.json.JSONObject
import java.net.URLEncoder


class MovieApiService(context: Context) {
    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        private const val API_KEY = BuildConfig.TMDB_API_KEY
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun searchMovies(
        query: String,
        onSuccess: (List<Movie>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$BASE_URL/search/movie?query=${URLEncoder.encode(query, "utf-8")}&include_adult=false&language=en-US"

        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                try {
                    val movies = parseMovieResponse(response)
                    onSuccess(movies)
                } catch (e: Exception) {
                    onError("Failed to parse response: ${e.message}")
                }
            },
            { error ->
                onError(error.message ?: "Unknown error")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "accept" to "application/json",
                    "Authorization" to "Bearer $API_KEY"
                )
            }
        }

        requestQueue.add(request)
    }

    private fun parseMovieResponse(response: JSONObject): List<Movie> {
        val movies = mutableListOf<Movie>()
        val results = response.getJSONArray("results")

        for (i in 0 until results.length()) {
            val movieJson = results.getJSONObject(i)
            movies.add(
                Movie(
                    id = movieJson.getInt("id"),
                    title = movieJson.getString("title"),
                    overview = movieJson.getString("overview"),
                    posterPath = movieJson.optString("poster_path", null),
                    releaseDate = movieJson.getString("release_date"),
                    voteAverage = movieJson.getDouble("vote_average")
                    // Add other fields as needed
                )
            )
        }
        return movies
    }
}