package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BaseView
import io.reactivex.Observable

interface ListView<T, VS : ListViewStateContainer<VS, T>> : BaseView<VS> {
    fun itemClicks(): Observable<Long>
    fun itemLongClicks(): Observable<Long>
}
