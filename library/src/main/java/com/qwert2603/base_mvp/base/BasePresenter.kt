package com.qwert2603.base_mvp.base

import android.support.annotation.CallSuper
import com.qwert2603.base_mvp.util.DisposableList
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
            disposableListModel.disposeAll()
            updateView()
        }

    protected var view: V? = null

    private var isViewReady = false

    private val disposableListModel = DisposableList()
    private val disposableListModelProcesses = DisposableList()
    protected val compositeDisposableView = CompositeDisposable()
    protected val compositeDisposableRxBus = CompositeDisposable()

    protected var loadingError = false
    protected var showLoadingError = true
    protected fun loading() = disposableListModel.isRunning()
    protected fun modelProcessing() = disposableListModelProcesses.isRunning()

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
        disposableListModel.disposeAll()
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
        view.showProcessingModel(modelProcessing())

        val canRefreshNow = model != null && !modelProcessing() && canSwipeRefresh
        view.setSwipeRefreshConfig(canRefreshNow, canRefreshNow && loading())

        if (noModel) {
            view.showLayerModel()
            return
        }

        val model = model
        if (model == null) {
            if (!loading() && !loadingError) view.showLayerNothing()
            if (loadingError) if (showLoadingError) view.showLayerLoadingError() else view.showLayerNothing()
            if (loading()) view.showLayerLoading()
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
        disposableListModel.disposeAll()
        loadingError = false
        modelSource()
                .subscribe({ m, throwable ->
                    loadingError = throwable != null
                    m?.let { onModelLoadSuccess(it) }
                    throwable?.let {
                        onModelLoadError(it)
                        updateView()
                    }
                })
                .addTo(disposableListModel)
        updateView()
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
        if (loading()) return
        loadModel()
    }

    fun <T> processModel(single: Single<T>, onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        single.subscribe { t: T?, throwable: Throwable? ->
            updateView()
            t?.let(onSuccess)
            throwable?.let {
                LogUtils.e(t = throwable)
                onError(it)
            }
        }.addTo(disposableListModelProcesses)
        updateView()
    }

    fun processModel(completable: Completable, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        completable._subscribe { throwable: Throwable? ->
            updateView()
            if (throwable == null) onSuccess()
            else {
                LogUtils.e(t = throwable)
                onError(throwable)
            }
        }.addTo(disposableListModelProcesses)
        updateView()
    }
}
