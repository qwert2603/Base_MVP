package com.qwert2603.base_mvp.base.list

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.TextView
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.base.BaseViewImpl
import com.qwert2603.base_mvp.base.ViewLayer
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.showIfNotYet
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_list.view.*

abstract class ListViewImpl<VS : ListViewStateContainer<T>, T : IdentifiableLong, V : ListView<T, VS>, P : ListPresenter<T, V, VS>>
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseViewImpl<VS, V, P>(context, attrs, defStyleAttr), ListView<T, VS> {

    companion object ViewAnimatorPositions {
        private const val POSITION_EMPTY = 0
        private const val POSITION_NOTHING_FOUND = 1
        private const val POSITION_LIST = 2
    }

    override fun swipeRefreshLayout(): SwipeRefreshLayout = fragment_list_swipeRefreshLayout

    protected abstract val adapter: BaseRecyclerViewAdapter<T, *>

    private lateinit var itemClickSubject: PublishSubject<Long>
    private lateinit var itemLongClickSubject: PublishSubject<Long>

    open protected fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun onViewCreated() {
        super.onViewCreated()

        list_recyclerView.layoutManager = createLayoutManager()
        (list_recyclerView.layoutManager as? LinearLayoutManager)?.initialPrefetchItemCount = 6

        itemClickSubject = PublishSubject.create()
        itemLongClickSubject = PublishSubject.create()

        adapter.clickListener = { itemClickSubject.onNext(it) }
        adapter.longClickListener = { itemLongClickSubject.onNext(it) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        list_recyclerView.adapter = adapter
    }

    override fun onDetachedFromWindow() {
        list_recyclerView.apply { postDelayed({ adapter = null }, 2000) }
        super.onDetachedFromWindow()
    }

    override fun render(vs: VS) {
        super.render(vs)

        if (vs.baseViewState.viewLayer != ViewLayer.MODEL) return

        val listViewState = vs.listViewState
        if (listViewState.listState == ListState.ITEMS) {
            adapter.modelList = listViewState.list
        }
        list_ViewAnimator.showIfNotYet(when (listViewState.listState) {
            ListState.EMPTY -> POSITION_EMPTY
            ListState.NOTHING_FOUND -> POSITION_NOTHING_FOUND
            ListState.ITEMS -> POSITION_LIST
        })
        if (listViewState.scrollToTop.getFlag(listViewState.id)) {
            list_recyclerView.apply { post { scrollToPosition(0) } }
        }
    }

    override fun itemClicks() = itemClickSubject

    override fun itemLongClicks() = itemLongClickSubject

    val _list_recyclerView: RecyclerView get() = list_recyclerView
    val _list_empty_TextView: TextView get() = list_empty_TextView
    val _list_ViewAnimator: ViewAnimator get() = list_ViewAnimator
}