package com.example.moviejournal

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moviejournal.data.repository.WatchlistRepository
import com.example.moviejournal.utils.MediaPermissionsHelper
import com.example.moviejournal.utils.MediaPermissionsHelper.getRequiredPermissions
import com.example.moviejournal.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    private lateinit var watchlistRepository: WatchlistRepository
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        watchlistRepository = WatchlistRepository(applicationContext)
        preferencesManager = PreferencesManager(applicationContext)
        enableEdgeToEdge()
        setContent {
            MovieJournalApp(
                watchlistRepository = watchlistRepository,
                preferencesManager = preferencesManager,
                onRequestGallery = { checkMediaPermissions() }
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
}


