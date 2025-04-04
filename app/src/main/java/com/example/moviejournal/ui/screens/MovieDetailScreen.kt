package com.example.moviejournal.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.moviejournal.data.local.Movie
import com.example.moviejournal.data.repository.WatchlistRepository
import com.example.moviejournal.ui.components.NetworkImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBackClick: () -> Unit,
    watchlistRepository: WatchlistRepository,
    onWatchlistUpdated: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isOnWatchlist by remember { mutableStateOf(watchlistRepository.isOnWatchlist(movie.id)) }
    var rating by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    if (isOnWatchlist) {
        rating = watchlistRepository.getMovie(movie.id)?.rating ?: 0
        notes = watchlistRepository.getMovie(movie.id)?.notes ?: ""
    }

    if (showDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Check, contentDescription = "") },
            title = { Text(text = "Review saved!") },
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                })
                { Text("Confirm") }
            },
        )
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
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(16.dp)
                ) {
                    NetworkImage(
                        url = movie.fullPosterPath(),
                        contentDescription = "Poster for ${movie.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.67f)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    MovieContentSection(
                        movie = movie,
                        isOnWatchlist = isOnWatchlist,
                        rating = rating,
                        notes = notes,
                        onWatchlistToggle = {
                            if (isOnWatchlist) {
                                watchlistRepository.removeFromWatchlist(movie.id)
                            } else {
                                watchlistRepository.addToWatchlist(movie)
                            }
                            isOnWatchlist = !isOnWatchlist
                            onWatchlistUpdated()
                        },
                        onRatingChange = { rating = it },
                        onNotesChange = { notes = it },
                        onSaveReview = {
                            watchlistRepository.updateMovieReview(
                                movieId = movie.id,
                                rating = rating,
                                notes = notes
                            )
                            showDialog = true
                        }
                    )
                }
            }
        } else {
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
                        .aspectRatio(2f / 3f)
                )

                MovieContentSection(
                    movie = movie,
                    isOnWatchlist = isOnWatchlist,
                    rating = rating,
                    notes = notes,
                    onWatchlistToggle = {
                        if (isOnWatchlist) {
                            watchlistRepository.removeFromWatchlist(movie.id)
                        } else {
                            watchlistRepository.addToWatchlist(movie)
                        }
                        isOnWatchlist = !isOnWatchlist
                        onWatchlistUpdated()
                    },
                    onRatingChange = { rating = it },
                    onNotesChange = { notes = it },
                    onSaveReview = {
                        watchlistRepository.updateMovieReview(
                            movieId = movie.id,
                            rating = rating,
                            notes = notes
                        )
                        showDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun MovieContentSection(
    movie: Movie,
    isOnWatchlist: Boolean,
    rating: Int,
    notes: String,
    onWatchlistToggle: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveReview: () -> Unit
) {
    Column {
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

        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onWatchlistToggle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isOnWatchlist) "Remove from Watchlist" else "Add to Watchlist")
        }

        if (isOnWatchlist) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Your Rating",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                for (i in 1..5) {
                    IconButton(onClick = { onRatingChange(i) }) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "$i star",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Notes",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write your thoughts about this movie...") },
                maxLines = 5
            )

            Button(
                onClick = onSaveReview,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Review")
            }
        }
    }
}