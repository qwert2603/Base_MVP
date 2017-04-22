package com.qwert2603.base_mvp.base

import android.support.annotation.CallSuper
import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util._subscribe
import com.qwert2603.base_mvp.util.addTo
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<M, V : BaseView> {

    open var model: M? = null
        set(model) {
            field = model
            compositeDisposableModel.clear()
            updateView()
        }

    protected var view: V? = null

    private var isViewReady = false

    private val compositeDisposableModel = CompositeDisposable()
    private val compositeDisposableModelProcesses = CompositeDisposable()
    protected val compositeDisposableView = CompositeDisposable()
    protected val compositeDisposableRxBus = CompositeDisposable()

    // todo: determine these states as if CompositeDisposables are disposed.
    // now (in rxjava 2.0.8) there is smth wrong with disposable created by subscribing to Single.
    // it returns false in isDisposed even after onSuccess call.
    protected var loadingError = false
    protected var showLoadingError = true
    protected var loading = false
    protected var modelProcesses = 0
        set(value) {
            if (value != field) {
                field = value
                updateView()
            }
        }

    private val actionToApplyView: MutableList<(V) -> Unit> = mutableListOf()

    protected open val canSwipeRefresh = false

    protected open val noModel = false

    @CallSuper
    open fun bindView(view: V) {
        this.view = view
    }

    @CallSuper
    open fun unbindView() {
        if (isViewReady) {
            onViewNotReady()
        }
        view = null
        compositeDisposableModel.clear()
        compositeDisposableRxBus.clear()
        actionToApplyView.clear()
    }

    @CallSuper
    open fun onViewReady(view: V) {
        isViewReady = true

        actionToApplyView.forEach { view.apply(it) }
        actionToApplyView.clear()

        updateView()
    }

    @CallSuper
    open fun onViewNotReady() {
        isViewReady = false
        compositeDisposableView.clear()
    }

    fun updateView() {
        view?.apply {
            if (isViewReady) {
                onUpdateView(this)
            }
        }
    }

    @CallSuper
    open protected fun onUpdateView(view: V) {
        view.showProcessingModel(modelProcesses != 0)

        val canRefreshNow = model != null && modelProcesses == 0 && canSwipeRefresh
        view.setSwipeRefreshConfig(canRefreshNow, canRefreshNow && loading)

        if (noModel) {
            view.showLayerModel()
            return
        }

        val model = model
        if (model == null) {
            if (!loading && !loadingError) view.showLayerNothing()
            if (loadingError) if (showLoadingError) view.showLayerLoadingError() else view.showLayerNothing()
            if (loading) view.showLayerLoading()
        } else {
            view.showLayerModel()
            onUpdateViewWithModel(view, model)
        }
    }

    @CallSuper
    open protected fun onUpdateViewWithModel(view: V, model: M) {
    }

    protected fun applyViewASAP(action: V.() -> Unit) {
        if (isViewReady) {
            view?.apply(action)
        } else {
            actionToApplyView.add(action)
        }
    }

    @Suppress("UNCHECKED_CAST")
    open protected fun modelSource(): Single<M> = Single.just(Any() as M)

    fun loadModel() {
        compositeDisposableModel.clear()
        loadingError = false
        loading = true
        updateView()
        modelSource()
                .subscribe({ m, throwable ->
                    loading = false
                    loadingError = throwable != null
                    m?.let { onModelLoadSuccess(it) }
                    throwable?.let {
                        onModelLoadError(it)
                        updateView()
                    }
                })
                .addTo(compositeDisposableModel)
    }

    @CallSuper
    open protected fun onModelLoadSuccess(m: M) {
        model = m
    }

    @CallSuper
    open protected fun onModelLoadError(throwable: Throwable) {
        LogUtils.e(t = throwable)
        showLoadingError = true
        model?.let { applyViewASAP { notifyRefreshingError() } }
    }

    open fun onReloadClicked() {
        if (loading) return
        loadModel()
    }

    fun <T> processModel(single: Single<T>, onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        single.subscribe { t: T?, throwable: Throwable? ->
            --modelProcesses
            t?.let(onSuccess)
            throwable?.let {
                LogUtils.e(t = throwable)
                onError(it)
            }
        }.addTo(compositeDisposableModelProcesses)
        ++modelProcesses
    }

    fun processModel(completable: Completable, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        completable._subscribe { throwable: Throwable? ->
            --modelProcesses
            if (throwable == null) onSuccess()
            else {
                LogUtils.e(t = throwable)
                onError(throwable)
            }
        }.addTo(compositeDisposableModelProcesses)
        ++modelProcesses
    }
}
