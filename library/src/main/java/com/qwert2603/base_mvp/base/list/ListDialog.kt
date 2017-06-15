package com.qwert2603.base_mvp.base.list

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.base.BaseDialog
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.fragment_list.view.*

abstract class ListDialog<T : IdentifiableLong, V : ListView<T>, P : ListPresenter<T, *, V>>
    : BaseDialog<V, P>(), ListView<T> {

    companion object ViewAnimatorPositions {
        private const val POSITION_EMPTY = 0
        private const val POSITION_NOTHING_FOUND = 1
        private const val POSITION_LIST = 2
    }

    protected abstract val adapter: BaseRecyclerViewAdapter<T, *>

    open protected fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    override fun createView(): View {
        val view = super.createView()

        dialogView.list_recyclerView.layoutManager = createLayoutManager()
        dialogView.list_recyclerView.adapter = adapter
        (dialogView.list_recyclerView.layoutManager as? LinearLayoutManager)?.initialPrefetchItemCount = 6

        dialogView.fragment_list_swipeRefreshLayout.isEnabled = false

        return view
    }

    override fun showEmpty() {
        dialogView.list_ViewAnimator.showIfNotYet(POSITION_EMPTY)
    }

    override fun showNothingFound() {
        dialogView.list_ViewAnimator.showIfNotYet(POSITION_NOTHING_FOUND)
    }

    override fun showList(list: List<T>) {
        adapter.modelList = list
        dialogView.list_ViewAnimator.showIfNotYet(POSITION_LIST)
    }

    override fun updateItem(id: Long) {
        (dialogView.list_recyclerView.findViewHolderForItemId(id) as? BaseRecyclerViewHolder<*, *, *>)?.updateView()
    }

    override fun updateVisibleItems() {
        (0..dialogView.list_recyclerView.childCount)
                .map { dialogView.list_recyclerView.getChildAt(it) }
                .filter { it != null }
                .map { dialogView.list_recyclerView.getChildViewHolder(it) }
                .forEach { (it as? BaseRecyclerViewHolder<*, *, *>)?.updateView() }
    }

    override fun scrollToTop() {
        dialogView.list_recyclerView.apply { post { scrollToPosition(0) } }
    }

    val _list_recyclerView: RecyclerView get() = dialogView.list_recyclerView
    val _list_empty_TextView: TextView get() = dialogView.list_empty_TextView
    val _list_ViewAnimator: ViewAnimator get() = dialogView.list_ViewAnimator

}
