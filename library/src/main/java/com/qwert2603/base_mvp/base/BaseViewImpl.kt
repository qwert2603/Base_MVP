package com.qwert2603.base_mvp.base

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.fragment_base.view.*

abstract class BaseViewImpl<V : BaseView, out P : BasePresenter<*, V>> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), BaseView {

    companion object ViewAnimatorPositions {
        private const val POSITION_LOADING = 0
        private const val POSITION_LOADING_ERROR = 1
        private const val POSITION_NOTHING = 2
        private const val POSITION_MODEL_VIEWS = 3
    }

    protected abstract val presenter: P

    abstract val layoutRes: Int
    open protected val toolbarRes = R.layout.toolbar_default

    open protected fun swipeRefreshLayout(): SwipeRefreshLayout? = null
    open protected fun viewForSnackbar(): View = fragment_base_ViewAnimator

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (childCount == 0) {
            this.inflate(R.layout.fragment_base, true)
            fragment_base_ViewAnimator.inflate(if (layoutRes != 0) layoutRes else R.layout.layout_empty_model, true)
            if (toolbarRes != 0) baseFragment_AppBarLayout.inflate(toolbarRes, true)

            fragment_base_retry_button.setOnClickListener { presenter.onReloadClicked() }

            swipeRefreshLayout()?.setColorSchemeColors(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
            swipeRefreshLayout()?.setOnRefreshListener { presenter.onReloadClicked() }

            onViewCreated()
        }

        @Suppress("UNCHECKED_CAST")
        this as V

        LogUtils.d("BaseViewImpl onAttachedToWindow $this")

        presenter.bindView(this)
        presenter.onViewReady(this)
    }

    override fun onDetachedFromWindow() {
        LogUtils.d("BaseViewImpl onDetachedFromWindow $this")

        presenter.onViewNotReady()
        presenter.unbindView()
        super.onDetachedFromWindow()
    }

    open protected fun onViewCreated() {}

    override fun showProcessingModel(processingModel: Boolean) {
        fragment_base_processingModel_FrameLayout.visibility = if (processingModel) View.VISIBLE else View.GONE
    }

    override fun showLayerLoading() {
        fragment_base_ViewAnimator.showIfNotYet(POSITION_LOADING)
    }

    override fun showLayerLoadingError() {
        fragment_base_ViewAnimator.showIfNotYet(POSITION_LOADING_ERROR)
    }

    override fun showLayerModel() {
        fragment_base_ViewAnimator.showIfNotYet(POSITION_MODEL_VIEWS)
    }

    override fun showLayerNothing() {
        fragment_base_ViewAnimator.showIfNotYet(POSITION_NOTHING)
    }

    override fun setSwipeRefreshConfig(canRefresh: Boolean, refreshing: Boolean) {
        swipeRefreshLayout()?.isEnabled = canRefresh
        swipeRefreshLayout()?.isRefreshing = refreshing
    }

    override fun notifyRefreshingError() {
        val snackbar = Snackbar.make(viewForSnackbar(), R.string.refreshing_error_text, Snackbar.LENGTH_SHORT)
                .setAction(R.string.retry_text, { presenter.onReloadClicked() })
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                onLoadingErrorSnackbarDismissed()
            }
        })
        snackbar.show()
    }

    protected open fun onLoadingErrorSnackbarDismissed() {}

    protected fun makeToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
    }

    val _fragment_base_ViewAnimator: ViewAnimator get() = fragment_base_ViewAnimator
    val _fragment_base_error_message: TextView get() = fragment_base_error_message

}