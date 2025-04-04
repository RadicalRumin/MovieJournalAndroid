package com.example.moviejournal.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

object ImageLoader {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun loadImage(
        url: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Exception) -> Unit = { _ -> }
    ): Job {
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
}



