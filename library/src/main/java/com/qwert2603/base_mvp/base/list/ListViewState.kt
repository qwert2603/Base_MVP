package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BaseStatePartialChanges
import com.qwert2603.base_mvp.base.BaseViewState
import com.qwert2603.base_mvp.base.BaseViewStateContainer

enum class ListState {
    EMPTY,
    NOTHING_FOUND,
    ITEMS
}

data class ListViewState<out T>(
        override val baseViewState: BaseViewState,
        val listState: ListState,
        val list: List<T>,
        val scrollToTop: Boolean
) : BaseViewStateContainer

interface ListViewStateContainer<out T> : BaseViewStateContainer {
    val listViewState: ListViewState<T>
}

interface ListStatePartialChanges : BaseStatePartialChanges {
    class LayerEmpty : ListStatePartialChanges
    class LayerNothingFound(val error: Throwable) : ListStatePartialChanges
    class LayerItems<out T>(val items: List<T>) : BaseStatePartialChanges.LayerModel<List<T>>(items)

    class ScrollToTop(val scrollToTop: Boolean)
}