package com.example.moviejournal.viewmodels

import androidx.compose.runtime.Composable

@Composable
fun AppNavHost(navController: NavHostController){
    NavHost(navController, startDestination = "watchlist"){
        composable("watchlist"){WatchlistScreen()}
        composable("detail/{movieId}"){}
    }
}