package com.qwert2603.base_mvp.base

interface BaseView {
    fun showLayerLoading()
    fun showLayerLoadingError()
    fun showLayerModel()
    fun showLayerNothing()

    fun showProcessingModel(processingModel: Boolean)

    fun setSwipeRefreshConfig(canRefresh: Boolean, refreshing: Boolean)
    fun notifyRefreshingError()
}