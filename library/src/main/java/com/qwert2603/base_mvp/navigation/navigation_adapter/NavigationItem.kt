package com.qwert2603.base_mvp.navigation.navigation_adapter

import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import com.qwert2603.base_mvp.model.IdentifiableLong

data class NavigationItem(
        @IdRes override val id: Long,
        @DrawableRes val iconRes: Int,
        @StringRes val titleRes: Int,
        val fragmentClass: Class<*>
) : IdentifiableLong

