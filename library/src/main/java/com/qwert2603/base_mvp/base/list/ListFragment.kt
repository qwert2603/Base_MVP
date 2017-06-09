package com.qwert2603.base_mvp.base.list

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.base.BaseFragment
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.showIfNotYet
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_list.*

abstract class ListFragment<VS : ListViewStateContainer<T>, T : IdentifiableLong, V : ListView<T, VS>, P : ListPresenter<T, V, VS>>
    : BaseFragment<VS, V, P>(), ListView<T, VS> {

    companion object ViewAnimatorPositions {
        private const val POSITION_EMPTY = 0
        private const val POSITION_NOTHING_FOUND = 1
        private const val POSITION_LIST = 2
    }

    override fun swipeRefreshLayout(): SwipeRefreshLayout = fragment_list_swipeRefreshLayout

    protected abstract val adapter: BaseRecyclerViewAdapter<T, *>

    open protected fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_recyclerView.layoutManager = createLayoutManager()
        list_recyclerView.adapter = adapter
        (list_recyclerView.layoutManager as? LinearLayoutManager)?.initialPrefetchItemCount = 6
    }

    override fun onDestroyView() {
        list_recyclerView.apply { postDelayed({ adapter = null }, 2000) }
        super.onDestroyView()
    }

    override fun render(vs: VS) {
        super.render(vs)

        val listViewState = vs.listViewState
        if (listViewState.listState == ListState.ITEMS) {
            adapter.modelList = listViewState.list
        }
        list_ViewAnimator.showIfNotYet(when (listViewState.listState) {
            ListState.EMPTY -> POSITION_EMPTY
            ListState.NOTHING_FOUND -> POSITION_NOTHING_FOUND
            ListState.ITEMS -> POSITION_LIST
        })
        if (listViewState.scrollToTop) {
            list_recyclerView.apply { post { scrollToPosition(0) } }
        }
    }

    override fun itemClicks(): Observable<Long> {
        //todo
        TODO()
    }

    override fun itemLongClicks(): Observable<Long> {
        //todo
        TODO()
    }

    val _list_recyclerView: RecyclerView get() = list_recyclerView
    val _list_empty_TextView: TextView get() = list_empty_TextView
    val _list_ViewAnimator: ViewAnimator get() = list_ViewAnimator
}