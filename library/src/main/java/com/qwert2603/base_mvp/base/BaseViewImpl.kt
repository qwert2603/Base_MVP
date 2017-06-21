package com.qwert2603.base_mvp.base

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewAnimator
import com.hannesdorfmann.mosby3.mvi.layout.MviFrameLayout
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.showIfNotYet
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_base.view.*

abstract class BaseViewImpl<VS : BaseViewStateContainer<VS>, V : BaseView<VS>, P : BasePresenter<V, VS>>
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : MviFrameLayout<V, P>(context, attrs, defStyleAttr), BaseView<VS> {

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (childCount == 0) {
            this.inflate(R.layout.fragment_base, true)
            fragment_base_ViewAnimator.inflate(if (layoutRes != 0) layoutRes else R.layout.layout_empty_model, true)
            if (toolbarRes != 0) baseFragment_AppBarLayout.inflate(toolbarRes, true)

            fragment_base_retry_button.setOnClickListener { refreshSubject.onNext(Any()) }

            swipeRefreshLayout()?.setColorSchemeColors(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
            swipeRefreshLayout()?.setOnRefreshListener { refreshSubject.onNext(Any()) }

            onViewCreated()
        }
    }

    open protected fun onViewCreated() {}

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
        if (baseViewState.refreshingError.getFlag(baseViewState.id)) {
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

    override fun refresh(): Observable<Any> = refreshSubject

    protected open fun onLoadingErrorSnackbarDismissed() {}

    protected fun makeToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
    }

    val _fragment_base_ViewAnimator: ViewAnimator get() = fragment_base_ViewAnimator
    val _fragment_base_error_message: TextView get() = fragment_base_error_message

}