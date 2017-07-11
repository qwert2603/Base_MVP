package com.qwert2603.base_mvp.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.CircularRevealDialogFragment
import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.dialog_base.view.*
import java.util.*

abstract class BaseDialog<V : BaseView, P : BasePresenter<*, V>> : CircularRevealDialogFragment(), BaseView {

    protected abstract var presenter: P

    abstract val layoutRes: Int
    lateinit protected var dialogView: View

    private var willBeRecreated = false

    private var processingModel = false
        set(value) {
            field = value
            updateButtonsEnable()
        }

    private var layerModel = false
        set(value) {
            field = value
            updateButtonsEnable()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val code = savedInstanceState.getInt(presenterCodeKey)
            @Suppress("UNCHECKED_CAST")
            presenter = loadPresenter(code) as P
        }
        @Suppress("UNCHECKED_CAST")
        presenter.bindView(this as V)
        willBeRecreated = false
    }

    override fun onDestroy() {
        if (!willBeRecreated) {
            presenter.unbindView()
        }
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
        willBeRecreated = true
        super.onSaveInstanceState(outState)
    }

    open protected fun createView(): View {
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_base, null)
        dialogView.dialog_base_ViewAnimator.inflate(if (layoutRes != 0) layoutRes else R.layout.layout_empty_model, true)
        dialogView.dialog_base_retry_button.setOnClickListener { presenter.onReloadClicked() }
        return dialogView
    }

    override fun showProcessingModel(processingModel: Boolean) {
        dialogView.dialog_base_processingModel_FrameLayout.visibility = if (processingModel) View.VISIBLE else View.GONE
        this.processingModel = processingModel
    }

    override fun showLayerLoading() {
        dialogView.dialog_base_ViewAnimator.showIfNotYet(POSITION_LOADING)
        layerModel = false
    }

    override fun showLayerLoadingError() {
        dialogView.dialog_base_ViewAnimator.showIfNotYet(POSITION_LOADING_ERROR)
        layerModel = false
    }

    override fun showLayerModel() {
        dialogView.dialog_base_ViewAnimator.showIfNotYet(POSITION_MODEL_VIEWS)
        layerModel = true
    }

    override fun showLayerNothing() {
        dialogView.dialog_base_ViewAnimator.showIfNotYet(POSITION_NOTHING)
        layerModel = false
    }

    protected fun updateButtonsEnable() {
        val alertDialog = dialog as? AlertDialog ?: return
        val enable = layerModel && !processingModel
        LogUtils.d("BaseDialog updateButtonsEnable alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL) == ${alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)}")
//        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = enable
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = enable
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = enable

    }

    override fun setSwipeRefreshConfig(canRefresh: Boolean, refreshing: Boolean) {}
    override fun notifyRefreshingError() {}

    protected fun makeToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
    }

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
