package com.qwert2603.base_mvp.base

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter

abstract class BasePresenter<V : BaseView<VS>, VS : BaseViewStateContainer> : MviBasePresenter<V, VS>() {

    protected fun applyViewASAP(action: V.() -> Unit) {
        //todo
    }


}
