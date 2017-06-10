package com.qwert2603.base_mvp.base

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface BaseView<in VS : BaseViewStateContainer> : MvpView {
    fun render(vs: VS)

    fun load(): Observable<Any>
    fun refresh(): Observable<Any>
}