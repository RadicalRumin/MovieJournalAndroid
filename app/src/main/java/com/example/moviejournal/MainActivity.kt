package com.example.moviejournal

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.moviejournal.data.repository.WatchlistRepository
import com.example.moviejournal.utils.MediaPermissionsHelper
import com.example.moviejournal.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    private lateinit var watchlistRepository: WatchlistRepository
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        watchlistRepository = WatchlistRepository(applicationContext)
        enableEdgeToEdge()
        setContent {
           MovieJournalApp(watchlistRepository = watchlistRepository, preferencesManager = preferencesManager)
        }
    }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) {
            openImagePicker()
        } else {
            showPermissionDeniedMessage()
        }
    }

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { handleSelectedImage(it) }
    }

    fun checkMediaPermissions() {
        val requiredPermissions = MediaPermissionsHelper.getRequiredPermissions()

        if (requiredPermissions.all { permission ->
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            }) {
            openImagePicker()
        } else if (MediaPermissionsHelper.shouldShowRationale(this)) {
            showPermissionRationale()
        } else {
            permissionsLauncher.launch(requiredPermissions)
        }
    }

    private fun openImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleSelectedImage(uri: Uri) {
        // Save URI to preferences
        preferencesManager.backgroundImageUri = uri.toString()

        // Optional: Take persistable permission
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Media Access Needed")
            .setMessage("This app needs access to your media to set a background image")
            .setPositiveButton("Continue") { _, _ ->
                permissionsLauncher.launch(MediaPermissionsHelper.getRequiredPermissions())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Permission denied - cannot access media",
            Toast.LENGTH_SHORT
        ).show()
    }
}


