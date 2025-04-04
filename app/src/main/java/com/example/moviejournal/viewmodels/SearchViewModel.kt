package com.example.moviejournal.viewmodels

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviejournal.data.api.MovieApiService
import com.example.moviejournal.data.local.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SearchViewModel(private val apiService: MovieApiService) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                apiService.searchMovies(
                    query = query,
                    onSuccess = { movies ->
                        _movies.value = movies
                        _isLoading.value = false
                    },
                    onError = { errorMessage ->
                        _error.value = errorMessage
                        _isLoading.value = false
                    }
                )
            }
            catch (_: SocketTimeoutException) {
                _error.value = "Connection timeout"
            } catch (_: UnknownHostException) {
                _error.value = "No internet connection"
            } finally {
                _isLoading.value = false
            }
        }
    }
}