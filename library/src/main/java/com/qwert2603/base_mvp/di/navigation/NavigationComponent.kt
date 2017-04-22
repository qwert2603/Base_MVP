package com.qwert2603.base_mvp.di.navigation

import com.qwert2603.base_mvp.navigation.BaseMainActivity
import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationItemViewHolder
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NavigationModule::class))
interface NavigationComponent {
    fun inject(navigationItemViewHolder: NavigationItemViewHolder)
    fun inject(navigationItemViewHolder: BaseMainActivity)
}