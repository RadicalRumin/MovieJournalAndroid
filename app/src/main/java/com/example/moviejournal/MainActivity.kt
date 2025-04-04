package com.example.moviejournal

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.moviejournal.data.repository.WatchlistRepository
import com.example.moviejournal.utils.MediaPermissionsHelper
import com.example.moviejournal.utils.MediaPermissionsHelper.getRequiredPermissions
import com.example.moviejournal.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    private lateinit var watchlistRepository: WatchlistRepository
    private lateinit var preferencesManager: PreferencesManager

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        watchlistRepository = WatchlistRepository(applicationContext)
        preferencesManager = PreferencesManager(applicationContext)
        enableEdgeToEdge()
        setContent {
            MovieJournalApp(
                watchlistRepository = watchlistRepository,
                preferencesManager = preferencesManager,
                onRequestGallery = { checkMediaPermissions() },
                onSaveImage = { url -> handleSaveImage(url) }
            )
        }
    }


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { handleSelectedImage(it) }
        }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            openImagePicker()
        } else {
            val shouldShowRationale = getRequiredPermissions().any { permission ->
                !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            }

            if (shouldShowRationale) {
                showPermissionDeniedDialog()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied - cannot access media",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("You've permanently denied media permissions. Please enable them in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun checkMediaPermissions() {
        val requiredPermissions = getRequiredPermissions()

        val permissionsToRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            openImagePicker()
        } else {
            if (MediaPermissionsHelper.shouldShowRationale(this)) {
                showPermissionRationale()
            } else {
                permissionsLauncher.launch(permissionsToRequest)
            }
        }
    }

    private fun openImagePicker() {
        // Example of Implicit intent
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleSelectedImage(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            preferencesManager.backgroundImageUri = uri.toString()

            Toast.makeText(this, "Background updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to set background", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "Error setting background", e)
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Media Access Needed")
            .setMessage("This app needs access to your media to set a background image")
            .setPositiveButton("Continue") { _, _ ->
                permissionsLauncher.launch(getRequiredPermissions())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private val downloadPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && currentDownloadUrl != null) {
            downloadImage(currentDownloadUrl!!)
        } else {
            MediaPermissionsHelper.getWritePermission()
        }
    }

    private fun handleSaveImage(url: String) {
        currentDownloadUrl = url
        val permission = MediaPermissionsHelper.getWritePermission()

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                downloadImage(url)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                showDownloadPermissionRationale()
            }
            else -> {
                downloadPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showDownloadPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Storage Permission Needed")
            .setMessage("This permission allows the app to save movie posters to your device's gallery")
            .setPositiveButton("Continue") { _, _ ->
                downloadPermissionLauncher.launch(MediaPermissionsHelper.getWritePermission())
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showPermissionDeniedMessage() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("To save images, please grant storage permission in app settings")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private var currentDownloadUrl by mutableStateOf<String?>(null)

    private fun downloadImage(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(url)
                bitmap?.let {
                    saveImageToGallery(it, "MoviePoster_${System.currentTimeMillis()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                currentDownloadUrl = null
            }
        }
    }

    private suspend fun downloadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doInput = true
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            currentDownloadUrl = null
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap, displayName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream!!)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    }
}


