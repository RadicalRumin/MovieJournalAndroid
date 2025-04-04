package com.example.moviejournal.utils

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

object MediaPermissionsHelper {
    fun getRequiredPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }

    fun shouldShowRationale(activity: Activity): Boolean {
        return getRequiredPermissions().any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
}