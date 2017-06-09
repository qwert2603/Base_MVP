package com.qwert2603.base_mvp.base.list

import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.model.IdentifiableLong

abstract class ListPresenter<out T : IdentifiableLong, V : ListView<T, VS>, VS : ListViewStateContainer<T>> : BasePresenter<V, VS>() {
}