package com.example.bingwallpaper.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideConfig : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Limit disk cache to 50MB
        val diskCacheSizeBytes: Long = 1024 * 1024 * 50 // 50 MB
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))
        
        // Limit memory cache to 20MB
        val memoryCacheSizeBytes: Long = 1024 * 1024 * 20 // 20 MB
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes))
    }

    // Disable manifest parsing to speed up Glide initialization
    override fun isManifestParsingEnabled(): Boolean = false
}
