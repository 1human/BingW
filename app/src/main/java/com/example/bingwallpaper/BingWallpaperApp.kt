package com.example.bingwallpaper

import android.app.Application
import com.google.android.material.color.DynamicColors

class BingWallpaperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply Monet dynamic colors to the entire app
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
