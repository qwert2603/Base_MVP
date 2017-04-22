package com.qwert2603.base_mvp.base.recyclerview.delegete_adapter

import com.qwert2603.base_mvp.model.IdentifiableLong

interface ViewType : IdentifiableLong {
    companion object {
        val VIEW_TYPE_TRACKING = 1
        val VIEW_TYPE_TRACKING_PROJECT = 2
    }

    val viewType: Int
}