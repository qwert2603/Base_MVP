package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BaseView
import io.reactivex.Observable

interface ListView<out T, in VS : ListViewStateContainer<T>> : BaseView<VS> {

    //todo: do we need them?
    fun itemClicks(): Observable<Long>
    fun itemLongClicks(): Observable<Long>
}
