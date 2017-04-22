package com.qwert2603.base_mvp.base.recyclerview.delegete_adapter

import android.view.ViewGroup
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder

interface DelegateAdapter<M : ViewType> {
    fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerViewHolder<M, *, *>
}