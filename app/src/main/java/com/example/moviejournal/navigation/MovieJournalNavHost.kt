package com.example.moviejournal.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moviejournal.data.api.MovieApiService
import com.example.moviejournal.data.local.Movie
import com.example.moviejournal.data.repository.WatchlistRepository
import com.example.moviejournal.ui.screens.MovieDetailScreen
import com.example.moviejournal.ui.screens.PreferencesScreen
import com.example.moviejournal.ui.screens.SearchScreen
import com.example.moviejournal.ui.screens.WatchlistScreen
import com.example.moviejournal.utils.PreferencesManager
import com.example.moviejournal.viewmodels.SearchViewModel
import kotlinx.serialization.json.Json


@Composable
fun MovieJournalNavHost(
    navController: NavHostController,
    watchlistRepository: WatchlistRepository,
    preferencesManager: PreferencesManager,
    modifier: Modifier = Modifier,
    onRequestGallery: () -> Unit
) {
    val json = Json { ignoreUnknownKeys = true }

    NavHost(
        navController = navController,
        startDestination = SearchScreen.route,
        modifier = modifier
    ) {
        composable(route = SearchScreen.route) {
            val searchViewModel = SearchViewModel(MovieApiService(LocalContext.current))
            SearchScreen(
                viewModel = searchViewModel,
                onMovieClick = { movie: Movie ->
                    navController.navigate(MovieDetailScreen.createRoute(movie))
                }
            )
        }

        composable(route = Watchlist.route) {
            WatchlistScreen(
                watchlistRepository = watchlistRepository,
                onMovieClick = { movie: Movie ->
                    navController.navigate(MovieDetailScreen.createRoute(movie))
                }
            )
        }

        composable(
            route = MovieDetailScreen.routePattern(),
            arguments = listOf(navArgument("movie_json") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val movieJson = backStackEntry.arguments?.getString("movie_json")
                ?.let { Uri.decode(it) }
                ?: throw IllegalStateException("Movie argument required")

            val movie = try {
                json.decodeFromString<Movie>(movieJson)
            } catch (_: Exception) {
                throw IllegalStateException("Invalid movie data")
            }

            MovieDetailScreen(
                movie = movie,
                onBackClick = { navController.popBackStack() },
                watchlistRepository = watchlistRepository
            )
        }

        composable("preferences") {
            PreferencesScreen(
                preferencesManager = preferencesManager,
                onRequestGallery = onRequestGallery,
                context = LocalContext.current
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

