package com.example.moviejournal

sealed interface MovieJournalDestination {
    val route: String
}

data object SearchScreen : MovieJournalDestination {
    override val route = "search"
}

data object Watchlist : MovieJournalDestination {
    override val route = "watchlist"
}

data object MovieDetailScreen : MovieJournalDestination {
    override val route = "movie/{movieId}"
    fun createRoute(movieId: Int) = "movie/$movieId"
}

val movieTabRowScreens = listOf(SearchScreen, Watchlist)
