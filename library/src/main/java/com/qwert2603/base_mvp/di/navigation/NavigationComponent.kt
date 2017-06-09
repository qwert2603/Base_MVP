package com.qwert2603.base_mvp.di.navigation

import com.qwert2603.base_mvp.navigation.BaseMainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NavigationModule::class))
interface NavigationComponent {
    fun inject(navigationItemViewHolder: BaseMainActivity)
}