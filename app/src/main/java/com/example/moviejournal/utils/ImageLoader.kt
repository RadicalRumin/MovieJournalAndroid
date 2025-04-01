package com.example.moviejournal.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

object ImageLoader {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun loadImage(
        context: Context,
        url: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Exception) -> Unit = { _ -> }
    ): Job {  // Now returns the Job
        return scope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    URL(url).openStream().use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                bitmap?.let {
                    withContext(Dispatchers.Main) {
                        onSuccess(it)
                    }
                } ?: run {
                    throw IOException("Failed to decode bitmap")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    fun cancelAll() {
        scope.coroutineContext.cancelChildren()
    }
}