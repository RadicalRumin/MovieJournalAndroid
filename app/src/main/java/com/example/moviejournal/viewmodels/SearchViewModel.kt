package com.example.moviejournal.viewmodels

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviejournal.ui.screens.SearchScreen
import com.example.moviejournal.ui.screens.WatchlistScreen

@Composable
fun AppNavHost(navController: NavHostController){
    NavHost(navController, startDestination = "watchlist"){
        composable("watchlist"){ SearchScreen(
            onMovieClick = TODO()
        )}
        composable("detail/{movieId}"){}
    }
}