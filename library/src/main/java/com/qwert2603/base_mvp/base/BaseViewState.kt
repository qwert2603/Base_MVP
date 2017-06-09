package com.qwert2603.base_mvp.base


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
        val refreshingError: Boolean
)

interface BaseViewStateContainer {
    val baseViewState: BaseViewState
}

sealed class PartialStateChanges {
    class LayerLoading : PartialStateChanges()
    class LayerError(val error: Throwable) : PartialStateChanges()
    class LayerModel<out M>(val model: M) : PartialStateChanges()
    class LayerNothing : PartialStateChanges()
}