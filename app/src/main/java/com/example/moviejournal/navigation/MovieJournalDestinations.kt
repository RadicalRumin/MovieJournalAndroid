package com.example.moviejournal.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import com.example.moviejournal.R
import com.example.moviejournal.data.local.Movie
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


sealed interface MovieJournalDestination {
    val route: String
    val icon: ImageVector
    val titleRes: Int
}

data object SearchScreen : MovieJournalDestination {
    override val route = "search"
    override val icon = Icons.Default.Search
    override val titleRes = R.string.search
}

data object Watchlist : MovieJournalDestination {
    override val route = "watchlist"
    override val icon = Icons.Default.Favorite
    override val titleRes = R.string.watchlist
}

data object PreferencesScreen : MovieJournalDestination {
    override val route = "preferences"
    override val icon = Icons.Default.Settings
    override val titleRes = R.string.preferencesScreen
}

data object MovieDetailScreen : MovieJournalDestination {
    private val json = Json { ignoreUnknownKeys = true }
    override val route = "movie_route"
    fun routePattern() = "$route/{movie_json}"
    fun createRoute(movie: Movie) = "$route/${Uri.encode(json.encodeToString(movie))}"
    override val icon = Icons.Default.Favorite
    override val titleRes = R.string.movieDetailScreen
}


val movieTabRowScreens = listOf(SearchScreen, Watchlist, PreferencesScreen)

fun NavDestination.shouldShowBottomBar(): Boolean {
    val route = this.route ?: return true
    return when {
        route.startsWith("movie_route") -> false // Exclude MovieDetailScreen
        else -> true
    }
}
