package com.example.moviejournal.data.api

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.example.moviejournal.data.local.Movie
import com.google.gson.Gson
import java.net.URLEncoder

class MovieApiService(context: Context) {


    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3"

        // After careful consideration it was decided that properly securing an API key only used for school assignments is not worth the effort.
        private const val API_KEY =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNjk2MzVjZTM3OTY0YmVkN2U2ZWU0YmY0YTE3NTIwZCIsIm5iZiI6MTc0MDA1NjczMy44NjcsInN1YiI6IjY3YjcyODlkMTFmZjAzNDA5ZWMzZmY4ZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.9m4t8K2mMMQMdjFe4wDp6Hje1WlVkEZfOXKIn1Q_6WA"
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun searchMovies(
        query: String,
        page: Int = 1,
        onSuccess: (List<Movie>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isNetworkAvailable()) {
            onError("No internet connection")
            return
        }


        val url = "$BASE_URL/search/movie" +
                "?query=${URLEncoder.encode(query, "utf-8")}" +
                "&page=$page" +
                "&include_adult=false" +
                "&language=en-US"

        val request = object : Request<MovieResponse>(
            Method.GET,
            url,
            { error ->
                onError(error.message ?: "Request failed")
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<MovieResponse> {
                return try {
                    val jsonString = String(response?.data ?: byteArrayOf(), Charsets.UTF_8)
                    val movieResponse = gson.fromJson(jsonString, MovieResponse::class.java)
                    Response.success(movieResponse, HttpHeaderParser.parseCacheHeaders(response))
                } catch (e: Exception) {
                    Response.error(ParseError(e))
                }
            }

            override fun deliverResponse(response: MovieResponse) {
                onSuccess(response.results)
            }

            override fun getHeaders(): Map<String, String> = mapOf(
                "accept" to "application/json",
                "Authorization" to "Bearer $API_KEY"
            )
        }

        requestQueue.add(request)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}