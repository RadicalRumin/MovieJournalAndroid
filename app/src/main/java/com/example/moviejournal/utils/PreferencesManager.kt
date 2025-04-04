package com.example.moviejournal.utils

import android.content.Context

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        "app_preferences",
        Context.MODE_PRIVATE
    )

    var backgroundImageUri: String?
        get() = sharedPreferences.getString("background_image_uri", null)
        set(value) {
            sharedPreferences.edit().apply {
                if (value != null) {
                    putString("background_image_uri", value)
                } else {
                    remove("background_image_uri")
                }
                commit()
            }
        }
}