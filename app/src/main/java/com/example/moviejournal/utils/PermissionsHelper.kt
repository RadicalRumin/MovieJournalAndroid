package com.example.moviejournal.utils

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

object MediaPermissionsHelper {
    fun getRequiredPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    }

    fun getWritePermission(): String {
        return Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun shouldShowRationale(activity: Activity): Boolean {
        return getRequiredPermissions().any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
}