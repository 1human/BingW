package com.example.bingwallpaper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bingwallpaper.databinding.ItemWallpaperBinding

class WallpaperAdapter(
    private val wallpapers: List<WallpaperData>,
    private val onItemClick: (WallpaperData) -> Unit
) : RecyclerView.Adapter<WallpaperAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wallpaper: WallpaperData) {
            binding.dateText.text = wallpaper.date
            
            Glide.with(binding.imageView.context)
                .load(wallpaper.thumbnailUrl)
                .centerCrop()
                .into(binding.imageView)

            binding.cardView.setOnClickListener {
                onItemClick(wallpaper)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWallpaperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wallpapers[position])
    }

    override fun getItemCount() = wallpapers.size
}
