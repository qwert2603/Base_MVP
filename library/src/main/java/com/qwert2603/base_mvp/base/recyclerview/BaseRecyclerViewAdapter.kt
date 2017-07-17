package com.qwert2603.base_mvp.base.recyclerview

import android.os.SystemClock
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.LogUtils

abstract class BaseRecyclerViewAdapter<M : IdentifiableLong, VH : BaseRecyclerViewHolder<M>> : RecyclerView.Adapter<VH>() {

    open var recyclerView: RecyclerView? = null

    open var modelList: List<M> = emptyList()
        set(value) {
            val oldList = field
            field = value

            val b = SystemClock.elapsedRealtime()
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = oldList.size
                override fun getNewListSize() = field.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == field[newItemPosition].id
                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == field[newItemPosition]
            })
            val time = SystemClock.elapsedRealtime() - b
            LogUtils.d("DiffUtil.calculateDiff $time ms")
            if (time > 10) {
                LogUtils.e("DiffUtil.calculateDiff is too long: $time ms")
            }
            if (LogUtils.enableLogging) {
                diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
                    override fun onMoved(fromPosition: Int, toPosition: Int) {
                        LogUtils.d("ListUpdateCallback onMoved $fromPosition $toPosition ${this@BaseRecyclerViewAdapter.javaClass}")
                    }

                    override fun onChanged(position: Int, count: Int, payload: Any?) {
                        LogUtils.d("ListUpdateCallback onChanged $position $count ${this@BaseRecyclerViewAdapter.javaClass}")
                    }

                    override fun onInserted(position: Int, count: Int) {
                        LogUtils.d("ListUpdateCallback onInserted $position $count ${this@BaseRecyclerViewAdapter.javaClass}")
                    }

                    override fun onRemoved(position: Int, count: Int) {
                        LogUtils.d("ListUpdateCallback onRemoved $position $count ${this@BaseRecyclerViewAdapter.javaClass}")
                    }
                })
            }
            diffResult.dispatchUpdatesTo(this@BaseRecyclerViewAdapter)
        }

    var clickListener: ((Long) -> Unit)? = null
    var longClickListener: ((Long) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override final fun setHasStableIds(hasStableIds: Boolean) {
        if (hasStableIds == this.hasStableIds()) return
        super.setHasStableIds(hasStableIds)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.adapter = this
        holder.bind(modelList[position])
    }

    override fun getItemCount() = modelList.size

    override fun getItemId(position: Int) = modelList[position].id
}