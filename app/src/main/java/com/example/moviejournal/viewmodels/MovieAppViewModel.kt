package com.example.moviejournal.viewmodels

import androidx.lifecycle.ViewModel
import com.example.moviejournal.data.local.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class MovieAppViewModel: ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())

    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()
}