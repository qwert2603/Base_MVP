package com.qwert2603.base_mvp.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ViewAnimator
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.showIfNotYet
import kotlinx.android.synthetic.main.view_show_all_button_with_loading.view.*

class ShowAllButtonWithLoading @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null)
    : ViewAnimator(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_show_all_button_with_loading, this, true)
        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ShowAllButtonWithLoading)

            val showAllDrawable = typedArray.getDrawable(R.styleable.ShowAllButtonWithLoading_showAllDrawable)
            showAll_Button.setImageDrawable(showAllDrawable ?: ResourcesCompat.getDrawable(resources, R.drawable.ic_expand_more_black_24dp, null))

            val progressBarColorDefault = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
            val progressBarColor = typedArray.getColor(R.styleable.ShowAllButtonWithLoading_progressBarColor, progressBarColorDefault)
            loading_ProgressBar.indeterminateDrawable.setColorFilter(
                    progressBarColor,
                    PorterDuff.Mode.MULTIPLY
            )

            val progressBarMarginDefault = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
            val progressBarMargin = typedArray.getDimension(R.styleable.ShowAllButtonWithLoading_progressBarMargin, progressBarMarginDefault).toInt()
            (loading_ProgressBar.layoutParams as MarginLayoutParams).setMargins(
                    progressBarMargin,
                    progressBarMargin,
                    progressBarMargin,
                    progressBarMargin
            )

            typedArray.recycle()
        }
    }

    val _showAll_ImageButton: ImageButton get() = showAll_Button

    fun setState(state: State) {
        showIfNotYet(when (state) {
            State.SHOW_ALL -> 0
            State.LOADING -> 1
        })
    }

    enum class State {
        LOADING,
        SHOW_ALL
    }
}
