package com.qwert2603.base_mvp.base.recyclerview.delegete_adapter

import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder

abstract class BaseRecyclerViewAdapterDelegate<M : ViewType>
    : BaseRecyclerViewAdapter<M, BaseRecyclerViewHolder<M, *, *>>() {
    override fun getItemViewType(position: Int) = modelList[position].viewType
}