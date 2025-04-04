package com.example.moviejournal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviejournal.data.repository.WatchlistRepository
import com.example.moviejournal.navigation.MovieJournalNavHost
import com.example.moviejournal.navigation.SearchScreen
import com.example.moviejournal.navigation.movieTabRowScreens
import com.example.moviejournal.navigation.navigateSingleTopTo
import com.example.moviejournal.navigation.shouldShowBottomBar
import com.example.moviejournal.ui.navigation.MovieTabRow
import com.example.moviejournal.ui.theme.MovieJournalTheme
import com.example.moviejournal.utils.AsyncImage
import com.example.moviejournal.utils.PreferencesManager

@Composable
fun MovieJournalApp(
    watchlistRepository: WatchlistRepository,
    preferencesManager: PreferencesManager,
    onRequestGallery: () -> Unit
) {
    MovieJournalTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            movieTabRowScreens.find { it.route == currentDestination?.route } ?: SearchScreen

        var backgroundUri by remember { mutableStateOf(preferencesManager.backgroundImageUri) }
        LaunchedEffect(preferencesManager.backgroundImageUri) {
            backgroundUri = preferencesManager.backgroundImageUri
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Global background image
            backgroundUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Scaffold(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), //Semi transparent to show background image behind
                contentColor = MaterialTheme.colorScheme.onSurface,
                bottomBar = {
                    if (currentDestination?.shouldShowBottomBar() != false) {
                        MovieTabRow(
                            allScreens = movieTabRowScreens,
                            onTabSelected = { newScreen ->
                                navController.navigateSingleTopTo(newScreen.route)
                            },
                            currentScreen = currentScreen
                        )
                    }
                }
            ) { innerPadding ->
                MovieJournalNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    watchlistRepository = watchlistRepository,
                    preferencesManager = preferencesManager,
                    onRequestGallery = onRequestGallery
                )
            }
        }
    }
}

