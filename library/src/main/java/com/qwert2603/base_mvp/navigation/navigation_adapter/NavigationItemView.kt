package com.qwert2603.base_mvp.navigation.navigation_adapter

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.qwert2603.base_mvp.base.BaseView

interface NavigationItemView : BaseView {
    fun setIconRes(@DrawableRes iconRes: Int)
    fun setTitleRes(@StringRes titleRes: Int)
}