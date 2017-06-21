package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.base.BaseStatePartialChanges
import com.qwert2603.base_mvp.base.OneShotFlag
import com.qwert2603.base_mvp.base.ViewLayer
import com.qwert2603.base_mvp.model.IdentifiableLong

abstract class ListPresenter<T : IdentifiableLong, V : ListView<T, VS>, VS : ListViewStateContainer<VS, T>> : BasePresenter<V, VS>() {

    override fun viewStateReducer(viewState: VS, changes: BaseStatePartialChanges): ReduceResult<VS> {
        super.viewStateReducer(viewState, changes).let { if (it.changed) return it }

        @Suppress("UNCHECKED_CAST")
        val changedViewState = when (changes) {
            is ListStatePartialChanges.LayerEmpty -> viewState.changeListViewState(viewState.listViewState.copy(listState = ListState.EMPTY))
            is ListStatePartialChanges.LayerNothingFound -> viewState.changeListViewState(viewState.listViewState.copy(listState = ListState.NOTHING_FOUND))
            is ListStatePartialChanges.LayerItems<*> -> viewState.changeListViewState(viewState.listViewState.copy(
                    listState = ListState.ITEMS,
                    list = changes.items.map { it as T }
            ))
            is ListStatePartialChanges.ScrollToTop -> viewState.changeListViewState(viewState.listViewState.copy(scrollToTop = OneShotFlag()))
            else -> null
        }?.let { it.changeBaseViewState(it.baseViewState.copy(viewLayer = ViewLayer.MODEL)) }

        return ReduceResult(changedViewState ?: viewState, changedViewState != null)
    }
}