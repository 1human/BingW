package com.example.bingwallpaper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bingwallpaper.databinding.ItemViewerImageBinding

class ViewerAdapter(
    private val wallpapers: List<WallpaperData>,
    private val onImageClick: () -> Unit
) : RecyclerView.Adapter<ViewerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemViewerImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wallpaper: WallpaperData) {
            Glide.with(binding.imageView.context)
                .load(wallpaper.imageUrl)
                .into(binding.imageView)
            
            binding.imageView.setOnClickListener {
                onImageClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewerImageBinding.inflate(
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
