package com.example.bingwallpaper

data class WallpaperData(
    val imageUrl: String,
    val thumbnailUrl: String,
    val date: String,
    val copyright: String = "",
    val title: String = ""
) : java.io.Serializable
