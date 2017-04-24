package com.qwert2603.base_mvp.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.runOnLollipopOrHigher
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.toolbar_default.*

abstract class BaseFragment<V : BaseView, out P : BasePresenter<*, V>> : Fragment(), BaseView {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        @Suppress("UNCHECKED_CAST")
        presenter.bindView(this as V)
        LogUtils.d("BaseFragment ${hashCode()} ${this.javaClass} onCreate")
    }

    override fun onDestroy() {
        LogUtils.d("BaseFragment ${hashCode()} ${this.javaClass} onDestroy")
        presenter.unbindView()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d("onViewCreated ${hashCode()} $this")

        fragment_base_retry_button.setOnClickListener { presenter.onReloadClicked() }

        swipeRefreshLayout()?.setColorSchemeColors(
                ContextCompat.getColor(activity, R.color.colorPrimary),
                ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        )
        swipeRefreshLayout()?.setOnRefreshListener { presenter.onReloadClicked() }

        startPostponedEnterTransition()
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

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        postponeEnterTransition()

        val view = container?.inflate(R.layout.fragment_base)
        (view?.findViewById(R.id.fragment_base_ViewAnimator) as? ViewGroup)?.inflate(if (layoutRes != 0) layoutRes else R.layout.layout_empty_model, true)
        if (toolbarRes != 0) (view?.findViewById(R.id.baseFragment_AppBarLayout) as? ViewGroup)?.inflate(toolbarRes, true)

        runOnLollipopOrHigher {
            @SuppressLint("NewApi")
            (view as ViewGroup).isTransitionGroup = true
        }

        return view
    }

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

    protected val _fragment_base_ViewAnimator: ViewAnimator get() = fragment_base_ViewAnimator
    protected val _fragment_base_error_message: TextView get() = fragment_base_error_message
    protected val _toolbar: Toolbar get() = toolbar
}