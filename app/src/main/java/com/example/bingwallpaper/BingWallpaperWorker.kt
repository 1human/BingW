package com.example.bingwallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BingWallpaperWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val BING_API =
            "https://www.bing.com/HPImageArchive.aspx?format=js&idx=%d&n=1"
        private const val BING_HOST = "https://www.bing.com"

        fun fetchWeeklyImageUrls(context: Context): List<WallpaperData> {
            val wallpapers = mutableListOf<WallpaperData>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val region = prefs.getString("wallpaper_region", "zh-CN") ?: "zh-CN"

            try {
                val imageUrls = mutableSetOf<String>()
                for (i in 0 until 8) {
                    val apiUrl = BING_API.format(i) + "&mkt=$region"
                    val imageData = fetchImageData(apiUrl)
                    if (imageData != null) {
                        val imageUrl = imageData.first
                        if (imageUrls.contains(imageUrl)) break
                        imageUrls.add(imageUrl)

                        val copyright = imageData.second
                        val date = sdf.format(calendar.time)
                        // Use high-quality portrait version for both full image and thumbnail
                        // Bing's server will handle subject centering for these fixed dimensions
                        val fullUrl = "${imageUrl}_1080x1920.jpg"
                        val thumbUrl = "${imageUrl}_720x1280.jpg"
                        wallpapers.add(WallpaperData(fullUrl, thumbUrl, date, copyright))
                    }
                    calendar.add(Calendar.DATE, -1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return wallpapers
        }

        private fun fetchImageData(apiUrl: String): Pair<String, String>? {
            return try {
                val conn = URL(apiUrl).openConnection() as HttpURLConnection
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.requestMethod = "GET"

                val text = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val json = JSONObject(text)
                val images = json.getJSONArray("images")
                if (images.length() == 0) return null

                val first = images.getJSONObject(0)
                val urlBase = first.getString("urlbase")
                val copyright = first.optString("copyright", "")
                Pair("$BING_HOST$urlBase", copyright)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun downloadBitmapStatic(urlStr: String): Bitmap? {
            return try {
                val conn = URL(urlStr).openConnection() as HttpURLConnection
                conn.connectTimeout = 20000
                conn.readTimeout = 20000
                conn.requestMethod = "GET"

                conn.inputStream.use { input ->
                    BitmapFactory.decodeStream(input)
                }.also {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val region = prefs.getString("wallpaper_region", "zh-CN") ?: "zh-CN"

            val imageData = fetchTodayImageData(region) ?: return@withContext Result.retry()
            val imageUrlBase = imageData.first
            // Use portrait version for auto-update to ensure subject is centered
            val bitmap = downloadBitmap("${imageUrlBase}_1080x1920.jpg") ?: return@withContext Result.retry()

            setWallpaper(bitmap)
            bitmap.recycle()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun fetchTodayImageData(region: String): Pair<String, String>? {
        val apiUrl = BING_API.format(0) + "&mkt=$region"
        val conn = URL(apiUrl).openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.requestMethod = "GET"

        val text = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()

        val json = JSONObject(text)
        val images = json.getJSONArray("images")
        if (images.length() == 0) return null

        val first = images.getJSONObject(0)
        return Pair(BING_HOST + first.getString("urlbase"), first.optString("copyright", ""))
    }

    private fun downloadBitmap(urlStr: String): Bitmap? {
        val conn = URL(urlStr).openConnection() as HttpURLConnection
        conn.connectTimeout = 20000
        conn.readTimeout = 20000
        conn.requestMethod = "GET"

        return conn.inputStream.use { input ->
            BitmapFactory.decodeStream(input)
        }.also {
            conn.disconnect()
        }
    }

    private fun setWallpaper(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wallpaperManager.setBitmap(
                bitmap, null, true,
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            )
        } else {
            wallpaperManager.setBitmap(bitmap)
        }
    }
}
