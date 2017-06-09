package com.qwert2603.base_mvp.base.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import com.qwert2603.base_mvp.model.IdentifiableLong

abstract class BaseRecyclerViewHolder<in M : IdentifiableLong>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var adapter: BaseRecyclerViewAdapter<*, *>? = null

    init {
        itemView.setOnClickListener {
            adapter?.clickListener?.invoke(itemId)
        }
        itemView.setOnLongClickListener(View.OnLongClickListener {
            adapter?.longClickListener?.let {
                it.invoke(itemId)
                return@OnLongClickListener true
            }
            return@OnLongClickListener false
        })
    }

    abstract fun bind(m: M)
}
