package com.qwert2603.base_mvp.base.list

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.TextView
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.base.BaseViewImpl
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.fragment_list.view.*

abstract class ListViewImpl<T : IdentifiableLong, V : ListView<T>, out P : ListPresenter<T, *, V>> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseViewImpl<V, P>(context, attrs, defStyleAttr), ListView<T> {

    companion object ViewAnimatorPositions {
        private const val POSITION_EMPTY = 0
        private const val POSITION_NOTHING_FOUND = 1
        private const val POSITION_LIST = 2
    }

    override fun swipeRefreshLayout(): SwipeRefreshLayout = fragment_list_swipeRefreshLayout

    protected abstract val adapter: BaseRecyclerViewAdapter<T, *>

    open protected fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun onViewCreated() {
        super.onViewCreated()

        list_recyclerView.layoutManager = createLayoutManager()
        (list_recyclerView.layoutManager as? LinearLayoutManager)?.initialPrefetchItemCount = 6
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        list_recyclerView.adapter = adapter
    }

    override fun onDetachedFromWindow() {
        list_recyclerView.apply { postDelayed({ adapter = null }, 2000) }
        super.onDetachedFromWindow()
    }

    override fun showEmpty() {
        list_ViewAnimator.showIfNotYet(POSITION_EMPTY)
    }

    override fun showNothingFound() {
        list_ViewAnimator.showIfNotYet(POSITION_NOTHING_FOUND)
    }

    override fun showList(list: List<T>) {
        adapter.modelList = list
        list_ViewAnimator.showIfNotYet(POSITION_LIST)
    }

    override fun updateItem(id: Long) {
        (list_recyclerView.findViewHolderForItemId(id) as? BaseRecyclerViewHolder<*, *, *>)?.updateView()
    }

    override fun updateVisibleItems() {
        (0..list_recyclerView.childCount)
                .map { list_recyclerView.getChildAt(it) }
                .filter { it != null }
                .map { list_recyclerView.getChildViewHolder(it) }
                .forEach { (it as? BaseRecyclerViewHolder<*, *, *>)?.updateView() }
    }

    override fun scrollToTop() {
        list_recyclerView.apply { post { scrollToPosition(0) } }
    }

    val _list_recyclerView: RecyclerView get() = list_recyclerView
    val _list_empty_TextView: TextView get() = list_empty_TextView
    val _list_ViewAnimator: ViewAnimator get() = list_ViewAnimator
}