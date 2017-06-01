package com.qwert2603.base_mvp.di

import android.content.Context
import com.qwert2603.base_mvp.di.app_context.AppContextComponent
import com.qwert2603.base_mvp.di.app_context.AppContextModule
import com.qwert2603.base_mvp.di.app_context.DaggerAppContextComponent
import com.qwert2603.base_mvp.di.navigation.DaggerNavigationComponent
import com.qwert2603.base_mvp.di.navigation.NavigationComponent

abstract class BaseDIManager(private val appContext: Context) {
    val navigationComponent: NavigationComponent by lazy { DaggerNavigationComponent.create() }
    val appContextComponent: AppContextComponent by lazy { DaggerAppContextComponent.builder().appContextModule(AppContextModule(appContext)).build() }
}