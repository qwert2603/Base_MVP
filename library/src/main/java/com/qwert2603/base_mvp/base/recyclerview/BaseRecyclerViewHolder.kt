package com.qwert2603.base_mvp.base.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.base.BaseView
import com.qwert2603.base_mvp.model.IdentifiableLong

abstract class BaseRecyclerViewHolder<M : IdentifiableLong, V : BaseView, out P : BasePresenter<M, V>>(itemView: View)
    : RecyclerView.ViewHolder(itemView), BaseView {

    var adapter: BaseRecyclerViewAdapter<*, *>? = null

    init {
        itemView.setOnClickListener {
            adapter?.clickListener?.apply { onItemClicked(itemId) }
        }
        itemView.setOnLongClickListener(View.OnLongClickListener {
            adapter?.longClickListener?.apply {
                onItemLongClicked(itemId)
                return@OnLongClickListener true
            }
            return@OnLongClickListener false
        })
    }

    protected abstract val presenter: P

    internal fun setModel(m: M?) {
        presenter.model = m
    }

    open fun bindPresenter() {
        @Suppress("UNCHECKED_CAST")
        this as V
        presenter.bindView(this)
        presenter.onViewReady(this)
    }

    fun updateView() {
        presenter.updateView()
    }

    open fun unbindPresenter() {
        presenter.onViewNotReady()
        presenter.unbindView()
    }

    // List item doesn't show loading/error views, can't be processed and can't be swiped-to-refresh.
    // If needed, all these views/operations are available via containing view/fragment.
    override fun showLayerLoading() {}
    override fun showLayerLoadingError() {}
    override fun showLayerModel() {}
    override fun showLayerNothing() {}
    override fun showProcessingModel(processingModel: Boolean) {}
    override fun setSwipeRefreshConfig(canRefresh: Boolean, refreshing: Boolean) {}
    override fun notifyRefreshingError() {}
}
