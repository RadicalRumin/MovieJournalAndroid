import android.content.Context
import com.example.moviejournal.data.local.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class WatchlistRepository(private val context: Context) {
    private val sharedPrefs = context.getSharedPreferences("watchlist", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addToWatchlist(movie: Movie) {
        val watchlist = getWatchlist().toMutableList()
        if (!watchlist.any { it.id == movie.id }) {
            watchlist.add(movie)
            saveWatchlist(watchlist)
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        val watchlist = getWatchlist().filter { it.id != movieId }
        saveWatchlist(watchlist)
    }

    fun getWatchlist(): List<Movie> {
        val json = sharedPrefs.getString("movies", "[]") ?: "[]"
        val type = object : TypeToken<List<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isOnWatchlist(movieId: Int): Boolean {
        return getWatchlist().any { it.id == movieId }
    }

    private fun saveWatchlist(movies: List<Movie>) {
        sharedPrefs.edit() {
            putString("movies", gson.toJson(movies))
        }
    }
}