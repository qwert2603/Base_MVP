package com.qwert2603.base_mvp.di.app_context

import com.qwert2603.base_mvp.model.BasePreferenceHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppContextModule::class))
interface AppContextComponent {
    fun inject(basePreferenceHelper: BasePreferenceHelper)
}
