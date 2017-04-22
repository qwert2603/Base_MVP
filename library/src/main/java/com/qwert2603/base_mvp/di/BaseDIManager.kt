package com.qwert2603.base_mvp.di

import com.qwert2603.base_mvp.di.navigation.DaggerNavigationComponent
import com.qwert2603.base_mvp.di.navigation.NavigationComponent

abstract class BaseDIManager {

    private var navigationComponent: NavigationComponent? = null

    fun navigationComponent(): NavigationComponent = navigationComponent ?: DaggerNavigationComponent.create().apply { navigationComponent = this }
}