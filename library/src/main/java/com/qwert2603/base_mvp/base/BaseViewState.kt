package com.qwert2603.base_mvp.base

class OneShotFlag {
    private var flag = true
    fun get() = flag.also { flag = false }
}

enum class ViewLayer {
    LOADING,
    ERROR,
    MODEL,
    NOTHING
}

data class RefreshingConfig(
        val canRefresh: Boolean,
        val refreshing: Boolean
)

data class BaseViewState(
        val viewLayer: ViewLayer,
        val processingModel: Boolean,
        val refreshingConfig: RefreshingConfig,
        val refreshingError: OneShotFlag
)

interface BaseViewStateContainer {
    val baseViewState: BaseViewState
}

interface BaseStatePartialChanges {
    class LayerLoading : BaseStatePartialChanges
    class LayerError(val error: Throwable) : BaseStatePartialChanges
    class LayerModel : BaseStatePartialChanges
    class LayerNothing : BaseStatePartialChanges

    class ProcessingModel(val processing: Boolean) : BaseStatePartialChanges
    class Refreshing(val refreshingConfig: RefreshingConfig) : BaseStatePartialChanges
    class RefreshingError(val refreshingError: Boolean) : BaseStatePartialChanges
}