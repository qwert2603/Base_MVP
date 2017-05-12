package com.qwert2603.base_mvp

import android.app.Application
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.qwert2603.base_mvp.di.BaseDIManager

abstract class BaseApplication : Application() {

    companion object {
        lateinit var baseDiManager: BaseDIManager
    }

    override fun onCreate() {
        super.onCreate()

        baseDiManager = createDIManager()

        val displayImageOptions = DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_image_white)
                .build()
        val config = ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(imagesMemoryCacheMB() * 1024 * 1024)
                .diskCache(UnlimitedDiskCache(cacheDir))
                .defaultDisplayImageOptions(displayImageOptions)
                .build()
        ImageLoader.getInstance().init(config)
    }

    abstract fun createDIManager(): BaseDIManager

    open protected fun imagesMemoryCacheMB() = 48
}
