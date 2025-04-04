package com.example.moviejournal.utils

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat

object MediaPermissionsHelper {
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ - can request visual user selected permission
            arrayOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else {
            // Android 13 - need full media images permission
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    fun shouldShowRationale(activity: Activity): Boolean {
        return getRequiredPermissions().any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
}