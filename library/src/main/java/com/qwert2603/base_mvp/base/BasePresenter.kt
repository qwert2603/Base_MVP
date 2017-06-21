package com.qwert2603.base_mvp.base

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter

abstract class BasePresenter<V : BaseView<VS>, VS : BaseViewStateContainer<VS>> : MviBasePresenter<V, VS>() {

    data class ReduceResult<out VS>(
            val viewState: VS,
            val changed: Boolean
    )

    open fun viewStateReducer(viewState: VS, changes: BaseStatePartialChanges): ReduceResult<VS> {
        val changedViewState = when (changes) {
            is BaseStatePartialChanges.LayerLoading -> viewState.changeBaseViewState(viewState.baseViewState.copy(viewLayer = ViewLayer.LOADING))
            is BaseStatePartialChanges.LayerError -> viewState.changeBaseViewState(viewState.baseViewState.copy(viewLayer = ViewLayer.ERROR))
            is BaseStatePartialChanges.LayerModel -> viewState.changeBaseViewState(viewState.baseViewState.copy(viewLayer = ViewLayer.MODEL))
            is BaseStatePartialChanges.LayerNothing -> viewState.changeBaseViewState(viewState.baseViewState.copy(viewLayer = ViewLayer.NOTHING))
            is BaseStatePartialChanges.ProcessingModel -> viewState.changeBaseViewState(viewState.baseViewState.copy(processingModel = changes.processing))
            is BaseStatePartialChanges.Refreshing -> viewState.changeBaseViewState(viewState.baseViewState.copy(refreshingConfig = changes.refreshingConfig))
            is BaseStatePartialChanges.RefreshingError -> viewState.changeBaseViewState(viewState.baseViewState.copy(refreshingError = OneShotFlag()))
            else -> null
        }
        return ReduceResult(changedViewState ?: viewState, changedViewState != null)
    }

}
