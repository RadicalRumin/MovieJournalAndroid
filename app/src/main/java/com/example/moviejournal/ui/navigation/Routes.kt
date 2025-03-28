package com.example.moviejournal.ui.navigation

sealed class Routes(val route: String) {
    object Watchlist : Routes("watchlist")
    object Search : Routes("search")
    object Detail : Routes("detail/{movieId}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
}