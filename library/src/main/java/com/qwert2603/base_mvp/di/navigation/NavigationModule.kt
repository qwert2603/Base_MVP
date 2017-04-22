package com.qwert2603.base_mvp.di.navigation

import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationAdapter
import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationItemPresenter
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {
    @Provides fun navigationItemPresenter() = NavigationItemPresenter()
    @Provides fun navigationAdapter() = NavigationAdapter()
}