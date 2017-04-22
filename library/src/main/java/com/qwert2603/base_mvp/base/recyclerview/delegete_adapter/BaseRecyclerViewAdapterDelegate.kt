package com.qwert2603.base_mvp.base.recyclerview.delegete_adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.LogUtils
import java.util.*

abstract class BaseRecyclerViewAdapterDelegate<M> : BaseRecyclerViewAdapter<M, BaseRecyclerViewHolder<M, *, *>>() where M : IdentifiableLong, M : ViewType {

    @SuppressLint("UseSparseArrays")
    protected val delegateAdapters = HashMap<Int, DelegateAdapter<M>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<M, *, *>
            = delegateAdapters[viewType]!!.onCreateViewHolder(parent)
            .also { LogUtils.d("BaseRecyclerViewAdapterDelegate onCreateViewHolder $it") }

    override fun getItemViewType(position: Int): Int {
        return modelList[position].viewType
    }
}