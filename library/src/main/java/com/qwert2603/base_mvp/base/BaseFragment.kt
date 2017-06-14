package com.qwert2603.base_mvp.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
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
import com.qwert2603.base_mvp.navigation.BackStackFragment
import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.runOnLollipopOrHigher
import com.qwert2603.base_mvp.util.showIfNotYet
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.toolbar_default.*

abstract class BaseFragment<VS : BaseViewStateContainer, V : BaseView<VS>, P : BasePresenter<V, VS>> : BackStackFragment<V, P>(), BaseView<VS> {

    companion object ViewAnimatorPositions {
        private const val POSITION_LOADING = 0
        private const val POSITION_LOADING_ERROR = 1
        private const val POSITION_NOTHING = 2
        private const val POSITION_MODEL_VIEWS = 3
    }

    protected abstract val layoutRes: Int
    open protected val toolbarRes = R.layout.toolbar_default

    open protected fun swipeRefreshLayout(): SwipeRefreshLayout? = null
    open protected fun viewForSnackbar(): View = fragment_base_ViewAnimator

    private lateinit var refreshSubject: PublishSubject<Any>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d("onViewCreated ${hashCode()} $this")

        fragment_base_retry_button.setOnClickListener { refreshSubject.onNext(Any()) }

        swipeRefreshLayout()?.setColorSchemeColors(
                ContextCompat.getColor(activity, R.color.colorPrimary),
                ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        )
        swipeRefreshLayout()?.setOnRefreshListener { refreshSubject.onNext(Any()) }
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_base)
        (view?.findViewById(R.id.fragment_base_ViewAnimator) as? ViewGroup)?.inflate(if (layoutRes != 0) layoutRes else R.layout.layout_empty_model, true)
        if (toolbarRes != 0) (view?.findViewById(R.id.baseFragment_AppBarLayout) as? ViewGroup)?.inflate(toolbarRes, true)

        runOnLollipopOrHigher {
            @SuppressLint("NewApi")
            (view as ViewGroup).isTransitionGroup = true
        }

        refreshSubject = PublishSubject.create()

        return view
    }

    override fun render(vs: VS) {
        val baseViewState = vs.baseViewState
        fragment_base_ViewAnimator.showIfNotYet(when (baseViewState.viewLayer) {
            ViewLayer.LOADING -> POSITION_LOADING
            ViewLayer.ERROR -> POSITION_LOADING_ERROR
            ViewLayer.MODEL -> POSITION_MODEL_VIEWS
            ViewLayer.NOTHING -> POSITION_NOTHING
        })
        fragment_base_processingModel_FrameLayout.visibility = if (baseViewState.processingModel) View.VISIBLE else View.GONE
        swipeRefreshLayout()?.isEnabled = baseViewState.refreshingConfig.canRefresh
        swipeRefreshLayout()?.isRefreshing = baseViewState.refreshingConfig.refreshing
        if (baseViewState.refreshingError.get()) {
            val snackbar = Snackbar.make(viewForSnackbar(), R.string.refreshing_error_text, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry_text, { refreshSubject.onNext(Any()) })
            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    onLoadingErrorSnackbarDismissed()
                }
            })
            snackbar.show()
        }
    }

    override fun load(): Observable<Any> = Observable.just(Any())

    override fun refresh(): Observable<Any> = refreshSubject

    protected open fun onLoadingErrorSnackbarDismissed() {}

    protected fun makeToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
    }

    val _fragment_base_ViewAnimator: ViewAnimator get() = fragment_base_ViewAnimator
    val _fragment_base_error_message: TextView get() = fragment_base_error_message
    val _toolbar: Toolbar get() = toolbar
}