package com.example.moviejournal.ui.screens

import WatchlistRepository
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviejournal.data.local.Movie
import com.example.moviejournal.ui.components.NetworkImage
import com.example.moviejournal.viewmodels.MovieAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieAppViewModel,
    onBackClick: () -> Unit,
    watchlistRepository: WatchlistRepository,
    onWatchlistUpdated: () -> Unit = {}
) {


    var movie by remember { mutableStateOf<Movie?>(null) }
    var isOnWatchlist by remember { mutableStateOf(watchlistRepository.isOnWatchlist(movieId)) }
    var rating by remember { mutableStateOf(0) }
    var notes by remember { mutableStateOf("") }

    if (isOnWatchlist) {
        movie = watchlistRepository.getWatchlist().find { it.id == movieId }
    }
    else{
        movie = viewModel.movies.find { it.id == movieId }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(movie.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            NetworkImage(
                url = movie.fullPosterPath(),
                contentDescription = "Poster for ${movie.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f/3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title and Release Year
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = movie.releaseDate.take(4),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Watchlist Button
            Button(
                onClick = {
                    if (isOnWatchlist) {
                        watchlistRepository.removeFromWatchlist(movie.id)
                    } else {
                        watchlistRepository.addToWatchlist(movie)
                    }
                    isOnWatchlist = !isOnWatchlist
                    onWatchlistUpdated()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isOnWatchlist) "Remove from Watchlist" else "Add to Watchlist")
            }

            // Rating and Notes (only shown if on watchlist)
            if (isOnWatchlist) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Your Rating",
                    style = MaterialTheme.typography.titleMedium
                )

                // Star rating selector
                Row {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i star",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes field
                Text(
                    text = "Your Notes",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Write your thoughts about this movie...") },
                    maxLines = 5
                )

                // Save button
                Button(
                    onClick = {
                        // Save rating and notes
                        // You might want to create a data class for WatchlistMovie
                        // that extends Movie with these additional fields
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Review")
                }
            }
        }
    }
}