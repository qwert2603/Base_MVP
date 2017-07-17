package com.qwert2603.base_mvp.di

import android.content.Context
import com.qwert2603.base_mvp.di.app_context.AppContextComponent
import com.qwert2603.base_mvp.di.app_context.AppContextModule
import com.qwert2603.base_mvp.di.app_context.DaggerAppContextComponent

abstract class BaseDIManager(private val appContext: Context) {
    val appContextComponent: AppContextComponent by lazy { DaggerAppContextComponent.builder().appContextModule(AppContextModule(appContext)).build() }
}