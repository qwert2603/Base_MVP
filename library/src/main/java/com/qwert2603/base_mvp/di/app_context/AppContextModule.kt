package com.qwert2603.base_mvp.di.app_context

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppContextModule(private val appContext: Context) {
    @Provides @Singleton fun appContext(): Context = appContext
}
