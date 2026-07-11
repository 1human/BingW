package com.bumptech.glide

import android.content.Context
import com.example.bingwallpaper.utils.GlideConfig
import kotlin.Boolean
import kotlin.Suppress

internal class GeneratedAppGlideModuleImpl(
  @Suppress("UNUSED_PARAMETER")
  context: Context,
) : GeneratedAppGlideModule() {
  private val appGlideModule: GlideConfig
  init {
    appGlideModule = GlideConfig()
  }

  public override fun registerComponents(
    context: Context,
    glide: Glide,
    registry: Registry,
  ) {
    appGlideModule.registerComponents(context, glide, registry)
  }

  public override fun applyOptions(context: Context, builder: GlideBuilder) {
    appGlideModule.applyOptions(context, builder)
  }

  public override fun isManifestParsingEnabled(): Boolean = false
}
