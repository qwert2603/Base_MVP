package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BaseView
import io.reactivex.Observable

interface ListView<out T, in VS : ListViewStateContainer<T>> : BaseView<VS> {
    fun itemClicks(): Observable<Long>
    fun itemLongClicks(): Observable<Long>
}
