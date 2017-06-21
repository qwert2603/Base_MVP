package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BaseStatePartialChanges
import com.qwert2603.base_mvp.base.BaseViewStateContainer
import com.qwert2603.base_mvp.base.OneShotFlag
import com.qwert2603.base_mvp.base.ViewState_ID

enum class ListState {
    EMPTY,
    NOTHING_FOUND,
    ITEMS
}

data class ListViewState<out T>(
        val listState: ListState,
        val list: List<T>,
        val scrollToTop: OneShotFlag
) : ViewState_ID()

interface ListViewStateContainer<out T> : BaseViewStateContainer {
    val listViewState: ListViewState<T>
}

interface ListStatePartialChanges : BaseStatePartialChanges {
    class LayerEmpty : ListStatePartialChanges
    class LayerNothingFound : ListStatePartialChanges
    class LayerItems<out T>(val items: List<T>) : ListStatePartialChanges

    class ScrollToTop : ListStatePartialChanges
}