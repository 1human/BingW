package com.example.bingwallpaper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.bingwallpaper.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var isRestoringState = false

    companion object {
        private const val UNIQUE_WORK_NAME = "daily_bing_wallpaper"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRestoringState = true
        
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeWorkState()
    }

    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            binding.root.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        updateDisplayValues()
        setupVersionInfo()

        binding.cardLanguage.setOnClickListener { showLanguageDialog() }
        binding.cardRegion.setOnClickListener { showRegionDialog() }
        
        binding.switchAuto.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isRestoringState && buttonView.isPressed) {
                if (isChecked) {
                    if (isXiaomiDevice() && !isIgnoringBatteryOptimizations()) {
                        showBatteryOptimizationDialog()
                    }
                    enableDailyWork()
                    Toast.makeText(this, R.string.toast_auto_on, Toast.LENGTH_SHORT).show()
                } else {
                    disableDailyWork()
                    Toast.makeText(this, R.string.toast_auto_off, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateDisplayValues() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        binding.tvCurrentLanguage.text = if (currentLocales.isEmpty) {
            getString(R.string.lang_system)
        } else {
            val tag = currentLocales.toLanguageTags()
            when {
                tag.contains("zh", ignoreCase = true) -> getString(R.string.lang_cn)
                tag.contains("en", ignoreCase = true) -> getString(R.string.lang_en)
                else -> tag
            }
        }

        val regionCode = prefs.getString("wallpaper_region", "zh-CN")
        val regionName = when (regionCode) {
            "zh-CN" -> getString(R.string.region_cn)
            "en-US" -> getString(R.string.region_us)
            "ja-JP" -> getString(R.string.region_jp)
            "en-GB" -> getString(R.string.region_gb)
            "de-DE" -> getString(R.string.region_de)
            else -> regionCode
        }
        binding.tvCurrentRegion.text = getString(R.string.format_region_display, regionName, regionCode)
    }

    private fun setupVersionInfo() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            binding.tvVersion.text = getString(R.string.version_label, versionName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.lang_system),
            getString(R.string.lang_cn),
            getString(R.string.lang_en)
        )
        val tags = arrayOf("", "zh-CN", "en-US")
        
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_title_language)
            .setItems(languages) { _, which ->
                val appLocale: LocaleListCompat = if (tags[which].isEmpty()) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(tags[which])
                }
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
            .show()
    }

    private fun showRegionDialog() {
        val regions = arrayOf(
            getString(R.string.region_cn),
            getString(R.string.region_us),
            getString(R.string.region_jp),
            getString(R.string.region_gb),
            getString(R.string.region_de)
        )
        val codes = arrayOf("zh-CN", "en-US", "ja-JP", "en-GB", "de-DE")

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_title_region)
            .setItems(regions) { _, which ->
                getSharedPreferences("settings", MODE_PRIVATE).edit {
                    putString("wallpaper_region", codes[which])
                }
                updateDisplayValues()
            }
            .show()
    }

    private fun isXiaomiDevice(): Boolean = android.os.Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun showBatteryOptimizationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_battery_title)
            .setMessage(R.string.dialog_battery_msg)
            .setPositiveButton(R.string.btn_go_to_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun enableDailyWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val initialDelay = calculateInitialDelay()

        val request = PeriodicWorkRequestBuilder<BingWallpaperWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // If between 0:00 and 6:00 AM, trigger as soon as possible (after midnight)
        if (hour in 0..5) return 0

        // Otherwise, target 0:00 AM the next day
        val dueDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        return dueDate.timeInMillis - calendar.timeInMillis
    }

    private fun disableDailyWork() {
        WorkManager.getInstance(this).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    private fun observeWorkState() {
        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME)
            .observe(this) { infos ->
                val active = infos?.any {
                    it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
                } ?: false
                
                if (binding.switchAuto.isChecked != active) {
                    isRestoringState = true
                    binding.switchAuto.isChecked = active
                }
                isRestoringState = false
            }
    }
}
