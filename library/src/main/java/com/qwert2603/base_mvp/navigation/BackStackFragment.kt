package com.qwert2603.base_mvp.navigation

import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import com.qwert2603.base_mvp.base.BaseFragment
import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.base.BaseView
import com.qwert2603.base_mvp.util.LogUtils

abstract class BackStackFragment<V : BaseView, out P : BasePresenter<*, V>> : BaseFragment<V, P>() {
    fun getBackStackItem(): BackStackItem = arguments.getSerializable(BackStackItem.BACK_STACK_ITEM_KEY) as BackStackItem

    abstract fun title(): String

    open val closable = false

    private val editTexts: MutableList<EditText> = mutableListOf()
    @IdRes private var focusedEditTextId: Int? = null

    private var keyboardShown = false

    val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        view?.let { view ->
            val heightDiff = view.rootView.height - view.height
            keyboardShown = heightDiff > dpToPx(activity, 200f)
        }
    }

    override fun onResume() {
        super.onResume()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(onGlobalLayoutListener)
        (activity as? Navigation)?.onFragmentResumed(this@BackStackFragment)
    }

    override fun onPause() {
        super.onPause()
        if (!keyboardShown) {
            (activity as? Navigation)?.hideKeyboard()
            keyboardShown = false
        }
        (activity as? Navigation)?.onFragmentPaused(this@BackStackFragment)
        view?.viewTreeObserver?.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    override fun onDestroyView() {
        focusedEditTextId = null

        editTexts
                .filter(EditText::hasFocus)
                .firstOrNull()
                ?.let {
                    LogUtils.d("focusedEditTextId = it.id")
                    focusedEditTextId = it.id
                    (activity as? Navigation)?.hideKeyboard(removeFocus = false)
                }

        editTexts.clear()

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addEditTexts(view)

        focusedEditTextId?.let {
            (view.findViewById(it) as? EditText)?.let { editText ->
                (activity as? Navigation)?.showKeyboard(editText)
            }
        }
        focusedEditTextId = null
    }

    private fun addEditTexts(view: View) {
        when (view) {
            is EditText -> editTexts.add(view)
            is ViewGroup -> (0..view.childCount - 1).forEach { addEditTexts(view.getChildAt(it)) }
        }
    }

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }
}