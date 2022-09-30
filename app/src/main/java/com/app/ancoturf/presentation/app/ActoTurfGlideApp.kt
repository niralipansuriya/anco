package com.app.ancoturf.presentation.app

import android.content.Context
import android.util.Log
import com.app.ancoturf.BuildConfig
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class ActoTurfGlideApp : AppGlideModule() {
    override fun isManifestParsingEnabled() = false

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setLogLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
    }
}
