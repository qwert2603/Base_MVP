package com.qwert2603.base_mvp.base.recyclerview.delegete_adapter

import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.base_mvp.model.IdentifiableLong

abstract class BaseRecyclerViewAdapterDelegate<M>
    : BaseRecyclerViewAdapter<M, BaseRecyclerViewHolder<M, *, *>>() where M : IdentifiableLong, M : ViewType {
    override fun getItemViewType(position: Int) = modelList[position].viewType
}