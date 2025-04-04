package com.example.moviejournal.ui.screens

import com.example.moviejournal.data.repository.WatchlistRepository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.moviejournal.data.local.Movie
import com.example.moviejournal.ui.components.MovieCard

@Composable
fun WatchlistScreen(onMovieClick: (Movie) -> Unit, watchlistRepository: WatchlistRepository) {
    val movies by remember { mutableStateOf(watchlistRepository.getWatchlist()) }


    if (movies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Your watchlist is empty")
        }
    } else {
        LazyColumn {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }
    }
}