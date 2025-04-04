package com.example.moviejournal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.example.moviejournal.utils.PreferencesManager

@Composable
fun MovieJournalApp(
    watchlistRepository: WatchlistRepository,
    preferencesManager: PreferencesManager
) {
    MovieJournalTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            movieTabRowScreens.find { it.route == currentDestination?.route } ?: SearchScreen


        Scaffold(
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
                preferencesManager = preferencesManager
            )
        }
    }
}