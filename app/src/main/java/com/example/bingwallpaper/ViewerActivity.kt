package com.example.bingwallpaper

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bingwallpaper.databinding.ActivityViewerBinding
import com.example.bingwallpaper.utils.WallpaperHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewerBinding
    private lateinit var wallpapers: List<WallpaperData>
    private var isInfoVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isInfoVisible = true
        
        enableEdgeToEdge()
        binding = ActivityViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.cardInfo.translationY = -systemBars.bottom.toFloat()
            binding.btnSetWallpaper.translationY = -systemBars.bottom.toFloat()
            insets
        }

        val rawList = intent.getSerializableExtra("WALLPAPER_LIST", ArrayList::class.java)
        wallpapers = rawList?.filterIsInstance<WallpaperData>() ?: return finish()
        if (wallpapers.isEmpty()) return finish()
        
        val startPosition = intent.getIntExtra("START_POSITION", 0)

        setupViewPager(startPosition)

        binding.btnSetWallpaper.setOnClickListener {
            val currentPos = binding.viewPager.currentItem
            showSelectionMenu(wallpapers[currentPos].imageUrl)
        }
    }

    private fun setupViewPager(startPosition: Int) {
        val adapter = ViewerAdapter(wallpapers) {
            toggleUiMode()
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(startPosition, false)
        
        updateInfoText(startPosition)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateInfoText(position)
            }
        })
    }

    private fun updateInfoText(position: Int) {
        binding.tvCopyright.text = wallpapers[position].copyright
    }

    private fun toggleUiMode() {
        isInfoVisible = !isInfoVisible
        if (isInfoVisible) {
            binding.cardInfo.visibility = View.VISIBLE
            binding.btnSetWallpaper.visibility = View.GONE
        } else {
            binding.cardInfo.visibility = View.GONE
            binding.btnSetWallpaper.visibility = View.VISIBLE
        }
    }

    private fun showSelectionMenu(imageUrl: String) {
        val options = arrayOf(
            getString(R.string.menu_both),
            getString(R.string.menu_home),
            getString(R.string.menu_lock)
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.title_select_image)
            .setItems(options) { _, which ->
                val flags = when (which) {
                    0 -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                    1 -> WallpaperManager.FLAG_SYSTEM
                    2 -> WallpaperManager.FLAG_LOCK
                    else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                setAsWallpaper(imageUrl, flags)
            }
            .show()
    }

    private fun setAsWallpaper(urlStr: String, flags: Int) {
        Toast.makeText(this, R.string.toast_setting, Toast.LENGTH_SHORT).show()
        WallpaperHelper.setWallpaper(this, urlStr, flags) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, R.string.toast_success, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.toast_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
