package com.example.moviejournal.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.moviejournal.ui.components.MovieCard

@Composable
fun WatchlistScreen(onMovieClick: (Int) -> Unit) {
    LazyColumn {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) }  // Triggers navigation
            )
        }
    }
}