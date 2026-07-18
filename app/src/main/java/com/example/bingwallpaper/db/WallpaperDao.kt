package com.example.bingwallpaper.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpapers ORDER BY date DESC")
    fun getAll(): List<WallpaperEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(wallpapers: List<WallpaperEntity>)

    @Query("DELETE FROM wallpapers")
    fun deleteAll()

    @Query("DELETE FROM wallpapers WHERE date NOT IN (SELECT date FROM wallpapers ORDER BY date DESC LIMIT 8)")
    fun deleteOldWallpapers()
}
