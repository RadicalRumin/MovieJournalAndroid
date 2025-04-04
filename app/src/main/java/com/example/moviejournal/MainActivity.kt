package com.example.moviejournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moviejournal.data.repository.WatchlistRepository

class MainActivity : ComponentActivity() {
    private lateinit var watchlistRepository: WatchlistRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        watchlistRepository = WatchlistRepository(applicationContext)
        enableEdgeToEdge()
        setContent {
           MovieJournalApp(watchlistRepository = watchlistRepository)
        }
    }
}
