package com.example.moviejournal

import com.example.moviejournal.ui.screens.SearchScreen
import com.example.moviejournal.ui.screens.WatchlistScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviejournal.ui.screens.MovieDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.moviejournal.data.api.MovieApiService
import com.example.moviejournal.viewmodels.SearchViewModel

@Composable
fun MovieJournalNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SearchScreen.route,
        modifier = modifier
    ) {
        composable(route = SearchScreen.route) {
            val searchViewModel = SearchViewModel(MovieApiService(LocalContext.current))
            SearchScreen(
                viewModel = searchViewModel,
                onMovieClick = { movieId : Int ->
                    navController.navigate(MovieDetailScreen.createRoute(movieId))
                }
            )
        }

        composable(route = Watchlist.route) {
            WatchlistScreen(
                onMovieClick = { movieId ->
                    navController.navigate(MovieDetailScreen.createRoute(movieId))
                }
            )
        }

        composable(
            route = MovieDetailScreen.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
            ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailScreen(
                movieId = movieId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }