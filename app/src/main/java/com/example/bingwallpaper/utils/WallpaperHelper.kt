package com.example.bingwallpaper.utils

import android.app.WallpaperManager
import android.content.Context
import com.example.bingwallpaper.BingWallpaperWorker

object WallpaperHelper {

    fun setWallpaper(context: Context, urlStr: String, flags: Int, onComplete: (Boolean) -> Unit) {
        Thread {
            try {
                val bitmap = BingWallpaperWorker.downloadBitmapStatic(urlStr)
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap, null, true, flags)
                    bitmap.recycle()
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }.start()
    }
}
