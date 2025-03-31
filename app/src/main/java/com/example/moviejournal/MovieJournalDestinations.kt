package com.example.moviejournal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface MovieJournalDestination {
    val route: String
    val icon: ImageVector  // Add icon property
    val titleRes: Int      // Add string resource ID
}

data object SearchScreen : MovieJournalDestination {
    override val route = "search"
    override val icon = Icons.Default.Favorite  // Material icon
    override val titleRes = R.string.search  // String resource
}

data object Watchlist : MovieJournalDestination {
    override val route = "watchlist"
    override val icon = Icons.Default.Favorite  // Material icon
    override val titleRes = R.string.watchlist  // String resource
}

data object MovieDetailScreen : MovieJournalDestination {
    override val route = "movie/{movieId}"
    fun createRoute(movieId: Int) = "movie/$movieId"
    override val icon = Icons.Default.Favorite  // Material icon
    override val titleRes = R.string.watchlist  // String resource
}

val movieTabRowScreens = listOf(SearchScreen, Watchlist)
