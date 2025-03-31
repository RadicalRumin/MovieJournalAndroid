package com.example.moviejournal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviejournal.ui.navigation.MovieTabRow
import com.example.moviejournal.ui.theme.MovieJournalTheme
import androidx.compose.runtime.getValue

@Composable
fun MovieJournalApp() {
    MovieJournalTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = movieTabRowScreens.find { it.route == currentDestination?.route } ?: SearchScreen

        Scaffold(
            bottomBar = {
                MovieTabRow(
                    allScreens = movieTabRowScreens,
                    onTabSelected = { newScreen : MovieJournalDestination ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            MovieJournalNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}