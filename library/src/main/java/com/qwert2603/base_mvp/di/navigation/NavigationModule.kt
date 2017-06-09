package com.qwert2603.base_mvp.di.navigation

import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationAdapter
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {
    @Provides fun navigationAdapter() = NavigationAdapter()
}