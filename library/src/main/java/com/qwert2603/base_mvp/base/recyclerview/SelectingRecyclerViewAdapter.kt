package com.qwert2603.base_mvp.base.recyclerview

import android.support.transition.TransitionManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.inflate
import kotlinx.android.synthetic.main.item_selecting.view.*
import java.util.*

abstract class SelectingRecyclerViewAdapter<M : IdentifiableLong, VH : BaseRecyclerViewHolder<M>> : BaseRecyclerViewAdapter<M, VH>() {

    interface SelectionListener {
        fun onSelectionChanged(id: Long, selected: Boolean)
    }

    var selectionListener: SelectionListener? = null

    var recyclerView: RecyclerView? = null

    var selectionMode = false
        set(value) {
            if (value != field) {
                field = value
                if (!value) {
                    selectedIds.clear()
                }
                recyclerView?.let { recyclerView ->
                    recyclerView.setOnTouchListener { _, _ -> true }
                    recyclerView.postDelayed({ recyclerView.setOnTouchListener(null) }, 500)
                    TransitionManager.beginDelayedTransition(recyclerView)
                }
                notifyDataSetChanged()
            }
        }

    private val selectedIds = HashSet<Long>()

    fun setItemSelection(id: Long, select: Boolean) {
        if (select && selectedIds.add(id) || !select && selectedIds.remove(id)) {
            notifyItemChanged(modelList.indexOfFirst { it.id == id })
        }
    }

    override final fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = parent.inflate(R.layout.item_selecting) as ViewGroup
        val viewHolder = onCreateSelectingViewHolder(view, viewType)
        with(view) {
            selection_check_box.setOnCheckedChangeListener { _, checked ->
                val id = viewHolder.itemId
                if (checked) {
                    selectedIds.add(id)
                } else {
                    selectedIds.remove(id)
                }
                selectionListener?.onSelectionChanged(id, checked)
            }
        }
        return viewHolder
    }

    abstract fun onCreateSelectingViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.itemView) {
            selection_check_box.isChecked = selectedIds.contains(getItemId(position))
            selection_check_box.visibility = if (selectionMode) View.VISIBLE else View.GONE
        }
        super.onBindViewHolder(holder, position)
    }
}
