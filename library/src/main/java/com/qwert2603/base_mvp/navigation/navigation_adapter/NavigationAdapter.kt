package com.qwert2603.base_mvp.navigation.navigation_adapter

import android.view.ViewGroup
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.util.inflate

class NavigationAdapter : BaseRecyclerViewAdapter<NavigationItem, NavigationItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = NavigationItemViewHolder(parent.inflate(R.layout.item_navigation_menu))

    var selectedItemId: Long = 0
        set(value) {
            if (value != field) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        }
}