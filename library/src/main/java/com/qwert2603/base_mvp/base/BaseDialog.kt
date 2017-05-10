package com.qwert2603.base_mvp.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.fragment_base.view.*
import java.util.*

abstract class BaseDialog<V : BaseView, P : BasePresenter<*, V>> : DialogFragment(), BaseView {

    protected abstract var presenter: P

    abstract val layoutRes: Int
    lateinit protected var dialogView: View

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

    protected fun createView(): View {
        dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_base, null)
        dialogView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.fragment_base_ViewAnimator.inflate(if (layoutRes != 0) layoutRes else R.layout.layout_empty_model, true)
        return dialogView
    }

    override fun showProcessingModel(processingModel: Boolean) {
        dialogView.fragment_base_processingModel_FrameLayout.visibility = if (processingModel) View.VISIBLE else View.GONE
    }

    override fun showLayerLoading() {
        dialogView.fragment_base_ViewAnimator.showIfNotYet(POSITION_LOADING)
    }

    override fun showLayerLoadingError() {
        dialogView.fragment_base_ViewAnimator.showIfNotYet(POSITION_LOADING_ERROR)
    }

    override fun showLayerModel() {
        dialogView.fragment_base_ViewAnimator.showIfNotYet(POSITION_MODEL_VIEWS)
    }

    override fun showLayerNothing() {
        dialogView.fragment_base_ViewAnimator.showIfNotYet(POSITION_NOTHING)
    }

    override fun setSwipeRefreshConfig(canRefresh: Boolean, refreshing: Boolean) {}
    override fun notifyRefreshingError() {}

    companion object {

        private const val POSITION_LOADING = 0
        private const val POSITION_LOADING_ERROR = 1
        private const val POSITION_NOTHING = 2
        private const val POSITION_MODEL_VIEWS = 3

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
