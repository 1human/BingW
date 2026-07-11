package com.example.bingwallpaper.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey val date: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val copyright: String = ""
)
