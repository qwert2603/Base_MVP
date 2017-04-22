package com.qwert2603.base_mvp.base.list

import android.os.SystemClock
import com.qwert2603.base_mvp.base.list.ListModel
import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.base.list.ListView
import com.qwert2603.base_mvp.model.IdentifiableLong
import com.qwert2603.base_mvp.util.LogUtils

abstract class ListPresenter<T : IdentifiableLong, M : ListModel<T>, V : ListView<T>> : BasePresenter<M, V>() {

    open protected fun List<T>.filterAndSort(): List<T> = this@filterAndSort

    override fun onUpdateViewWithModel(view: V, model: M) {
        super.onUpdateViewWithModel(view, model)

        if (model.list.isEmpty()) {
            view.showEmpty()
            return
        }
        val b = SystemClock.elapsedRealtime()
        val processed = model.list.filterAndSort()
        val time = SystemClock.elapsedRealtime() - b
        LogUtils.d("${this.javaClass}.filterAndSort $time ms")
        if (time > 10) {
            LogUtils.e("${this.javaClass}.filterAndSort is too long: $time ms")
        }
        if (processed.isEmpty()) {
            view.showNothingFound()
        } else {
            view.showList(processed)
        }
    }

    fun updateVisibleItems() {
        applyViewASAP { updateVisibleItems() }
    }

}