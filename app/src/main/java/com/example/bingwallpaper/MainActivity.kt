package com.example.bingwallpaper

import android.app.WallpaperManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bingwallpaper.databinding.ActivityMainBinding
import com.example.bingwallpaper.utils.WallpaperHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private val wallpaperList = mutableListOf<WallpaperData>()
    private var currentRegion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val density = resources.displayMetrics.density
            val paddingBottom = (16 * density).toInt()
            binding.appBarLayout.setPadding(0, systemBars.top, 0, paddingBottom)
            binding.root.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.inflateMenu(R.menu.main_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                val intent = android.content.Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            } else false
        }

        setupRecyclerView()

        // Set dynamic colors for SwipeRefreshLayout to match FAB
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimaryContainer, typedValue, true)
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(typedValue.data)
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimaryContainer, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        binding.fabBackToTop.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
            binding.appBarLayout.setExpanded(true, true)
        }

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 500) {
                binding.fabBackToTop.show()
            } else {
                binding.fabBackToTop.hide()
            }
        }

        checkDailyRefresh()
    }

    private fun checkDailyRefresh() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lastDate = prefs.getString("last_refresh_date", "")
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        if (lastDate != today) {
            refreshData(autoSetWallpaper = true)
            prefs.edit { putString("last_refresh_date", today) }
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val region = prefs.getString("wallpaper_region", "zh-CN")

        if (currentRegion != region) {
            currentRegion = region
            loadWeeklyWallpapers()
        }
    }

    private fun setupRecyclerView() {
        wallpaperAdapter = WallpaperAdapter(wallpaperList) { wallpaperData ->
            val position = wallpaperList.indexOf(wallpaperData)
            val intent = android.content.Intent(this, ViewerActivity::class.java).apply {
                putExtra("WALLPAPER_LIST", ArrayList(wallpaperList))
                putExtra("START_POSITION", position)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = wallpaperAdapter
        }
    }

    private fun refreshData(autoSetWallpaper: Boolean = false) {
        val request = OneTimeWorkRequestBuilder<BingWallpaperWorker>()
            .setConstraints(networkConstraints())
            .build()
        WorkManager.getInstance(this).enqueue(request)
        loadWeeklyWallpapers(autoSetWallpaper)
    }

    private fun loadWeeklyWallpapers(autoSetWallpaper: Boolean = false) {
        val db = com.example.bingwallpaper.db.AppDatabase.getDatabase(this)
        val wallpaperDao = db.wallpaperDao()

        Thread {
            val cachedEntities = wallpaperDao.getAll()
            if (cachedEntities.isNotEmpty()) {
                val cachedWallpapers = cachedEntities.map {
                    WallpaperData(it.imageUrl, it.thumbnailUrl, it.date, it.copyright)
                }
                runOnUiThread {
                    wallpaperList.clear()
                    wallpaperList.addAll(cachedWallpapers)
                    wallpaperAdapter.notifyDataSetChanged()
                }
            }

            val freshWallpapers = BingWallpaperWorker.fetchWeeklyImageUrls(this@MainActivity)
            if (freshWallpapers.isNotEmpty()) {
                val entities = freshWallpapers.map {
                    com.example.bingwallpaper.db.WallpaperEntity(it.date, it.imageUrl, it.thumbnailUrl, it.copyright)
                }
                wallpaperDao.insertAll(entities)
                wallpaperDao.deleteOldWallpapers()

                runOnUiThread {
                    wallpaperList.clear()
                    wallpaperList.addAll(freshWallpapers)
                    wallpaperAdapter.notifyDataSetChanged()
                    binding.swipeRefreshLayout.isRefreshing = false
                    
                    if (autoSetWallpaper) {
                        setLatestWallpaper(freshWallpapers[0].imageUrl)
                    }
                }
            } else {
                runOnUiThread {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }.start()
    }

    private fun setLatestWallpaper(url: String) {
        Toast.makeText(this, getString(R.string.toast_daily_updating), Toast.LENGTH_SHORT).show()
        WallpaperHelper.setWallpaper(this, url, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, getString(R.string.toast_daily_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun networkConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}
