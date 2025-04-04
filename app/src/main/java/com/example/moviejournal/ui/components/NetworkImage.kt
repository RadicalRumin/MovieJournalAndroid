package com.example.moviejournal.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.moviejournal.utils.ImageLoader

@Composable
fun NetworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    },
    error: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.errorContainer)
        )
    }
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    DisposableEffect(url) {
        if (url.isNullOrEmpty()) {
            isLoading = false
            hasError = true
            onDispose { }
        } else {
            isLoading = true
            hasError = false
            bitmap = null

            val job = ImageLoader.loadImage(
                url = url,
                onSuccess = { image ->
                    bitmap = image
                    isLoading = false
                },
                onError = {
                    isLoading = false
                    hasError = true
                }
            )

            onDispose {
                job.cancel()
            }
        }

    }

    Box(modifier = modifier) {
        when {
            isLoading -> placeholder()
            hasError -> error()
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }

            else -> error()
        }
    }
}