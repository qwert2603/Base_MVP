package com.qwert2603.base_mvp.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

class FabHideBehavior @JvmOverloads constructor(context: Context? = null, attributeSet: AttributeSet? = null) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)

        if (dyConsumed > 0) {
            val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
            child.animate().translationY((child.height + layoutParams.bottomMargin).toFloat())
        } else if (dyConsumed < 0) {
            child.animate().translationY(0f)
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionButton, dependency: View): Boolean {
        child.animate().translationY(Math.min(0f, dependency.translationY - dependency.height))
        return super.onDependentViewChanged(parent, child, dependency)
    }
}