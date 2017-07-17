package com.qwert2603.base_mvp

import android.app.Application
import com.qwert2603.base_mvp.di.BaseDIManager

abstract class BaseApplication : Application() {

    companion object {
        lateinit var baseDiManager: BaseDIManager
    }

    override fun onCreate() {
        super.onCreate()

        baseDiManager = createDIManager()
    }

    abstract fun createDIManager(): BaseDIManager
}
