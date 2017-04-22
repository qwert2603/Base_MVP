package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BaseView

interface ListView<in T> : BaseView {
    fun showEmpty()
    fun showNothingFound()
    fun showList(list: List<T>)

    fun updateItem(id: Long)
    fun updateVisibleItems()

    fun scrollToTop()
}
