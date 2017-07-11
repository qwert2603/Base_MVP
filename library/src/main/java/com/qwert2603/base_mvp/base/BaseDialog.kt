package com.qwert2603.base_mvp.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Toast
import com.qwert2603.base_mvp.BuildConfig
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.*
import kotlinx.android.synthetic.main.dialog_base.view.*
import java.util.*

abstract class BaseDialog<V : BaseView, P : BasePresenter<*, V>> : DialogFragment(), BaseView {

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

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        LogUtils.d("BaseDialog onStart")
        if (arguments.getBoolean(START_ANIMATION_SHOWN, false)) return
        val startX = arguments.getInt(START_POSITION_X, -1)
        val startY = arguments.getInt(START_POSITION_Y, -1)
        LogUtils.d("BaseDialog onStart $startX $startY")
        if (startX == -1 || startY == -1) return
        runOnLollipopOrHigher {
            val decorView = dialog.window.decorView
            decorView.setOnPreDrawAction {
                val endRadius = Math.hypot(
                        resources.displayMetrics.widthPixels.toDouble(),
                        resources.displayMetrics.heightPixels.toDouble()
                ).toFloat()
                arguments.putBoolean(START_ANIMATION_SHOWN, true)
                LogUtils.d("BaseDialog onStart createCircularReveal $endRadius")
                ViewAnimationUtils.createCircularReveal(decorView, startX, startY, 0f, endRadius).start()
            }
        }
    }

    @SuppressLint("NewApi")
    protected fun runExitAnimation() {
        if (!arguments.getBoolean(START_ANIMATION_SHOWN, false)) return
        runOnLollipopOrHigher {
            val decorView = dialog.window.decorView
            val startX = arguments.getInt(START_POSITION_X, -1)
            val startY = arguments.getInt(START_POSITION_Y, -1)
            val endRadius = Math.hypot(
                    resources.displayMetrics.widthPixels.toDouble(),
                    resources.displayMetrics.heightPixels.toDouble()
            ).toFloat()
            ViewAnimationUtils.createCircularReveal(decorView, startX, startY, endRadius, 0f)
                    .also {
                        it.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                dismiss()
                            }
                        })
                    }
                    .start()
        }
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

        const val START_POSITION_X = BuildConfig.APPLICATION_ID + "START_POSITION_X"
        const val START_POSITION_Y = BuildConfig.APPLICATION_ID + "START_POSITION_Y"
        const val START_ANIMATION_SHOWN = BuildConfig.APPLICATION_ID + "START_ANIMATION_SHOWN"

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
