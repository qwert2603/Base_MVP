package com.qwert2603.base_mvp.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

abstract class BaseDialog<V : BaseView, P : BasePresenter<*, V>> : DialogFragment(), BaseView {

    protected abstract var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val code = savedInstanceState.getInt(presenterCodeKey)
            @Suppress("UNCHECKED_CAST")
            presenter = loadPresenter(code) as P
        }
        @Suppress("UNCHECKED_CAST")
        presenter.bindView(this as V)
    }

    override fun onDestroy() {
        presenter.unbindView()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        @Suppress("UNCHECKED_CAST")
        presenter.onViewReady(this as V)
    }

    override fun onPause() {
        presenter.onViewNotReady()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(presenterCodeKey, savePresenter(presenter))
        super.onSaveInstanceState(outState)
    }

    override fun showLayerLoading() {}
    override fun showLayerLoadingError() {}
    override fun showLayerModel() {}
    override fun showLayerNothing() {}
    override fun showProcessingModel(processingModel: Boolean) {}
    override fun setSwipeRefreshConfig(canRefresh: Boolean, refreshing: Boolean) {}
    override fun notifyRefreshingError() {}

    companion object {

        private val presenterCodeKey = "presenterCodeKey"

        @SuppressLint("UseSparseArrays")
        private val sPresenters = HashMap<Int, BasePresenter<*, *>>()

        private val sRandom = Random()

        private fun savePresenter(presenter: BasePresenter<*, *>): Int {
            var code: Int
            do {
                code = sRandom.nextInt()
            } while (sPresenters.containsKey(code))
            sPresenters.put(code, presenter)
            return code
        }

        private fun loadPresenter(code: Int): BasePresenter<*, *>? {
            val presenter = sPresenters[code]
            sPresenters.remove(code)
            return presenter
        }
    }

}
