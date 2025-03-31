package com.example.moviejournal.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.moviejournal.data.local.Movie
import com.example.moviejournal.ui.components.MovieCard

@Composable
fun WatchlistScreen(onMovieClick: (Int) -> Unit?) {
    val movies = remember { mutableStateListOf<Movie>() }

    LazyColumn {
        items(movies.size) { index ->
            MovieCard(
                movie = movies[index],
                onClick = { onMovieClick(movies[index].id) }
            )
        }
    }
}