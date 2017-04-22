package com.qwert2603.base_mvp.util

import io.reactivex.disposables.Disposable

open class DisposableList {
    var disposables = emptyList<Disposable>()

    open fun add(disposable: Disposable) {
        disposables = disposables.filter { !it.isDisposed } + listOf(disposable)
    }

    fun disposeAll() {
        disposables.forEach { it.dispose() }
        disposables = emptyList()
    }

    fun areAllDisposed() = disposables.all { it.isDisposed }
    fun isRunning() = disposables.any { !it.isDisposed }
}

fun Disposable.addTo(disposableList: DisposableList) {
    disposableList.add(this)
}