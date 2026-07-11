package com.example.bingwallpaper.utils

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import com.example.bingwallpaper.BingWallpaperWorker

object WallpaperHelper {

    fun setWallpaper(context: Context, urlStr: String, flags: Int, onComplete: (Boolean) -> Unit) {
        Thread {
            try {
                val bitmap = BingWallpaperWorker.downloadBitmapStatic(urlStr)
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, flags)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
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
