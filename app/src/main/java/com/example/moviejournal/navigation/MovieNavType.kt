package com.example.moviejournal.navigation


import android.os.Bundle
import androidx.navigation.NavType
import com.example.moviejournal.data.local.Movie
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class MovieNavType : NavType<Movie>(isNullableAllowed = false) {
    private val json = Json { ignoreUnknownKeys = true }

    override fun get(bundle: Bundle, key: String): Movie? {
        return bundle.getString(key)?.let { json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Movie {
        return json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: Movie) {
        bundle.putString(key, json.encodeToString(value))
    }
}