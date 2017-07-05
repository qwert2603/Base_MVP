package com.qwert2603.base_mvp.util

import android.annotation.TargetApi
import android.os.Build
import android.transition.Transition
import android.view.View
import android.view.ViewTreeObserver

fun runOnLollipopOrHigher(action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        action()
    }
}

fun View.setOnPreDrawAction(action: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            action()
            return true
        }
    })
}


@TargetApi(Build.VERSION_CODES.KITKAT)
open class TransitionListenerAdapter : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition) {}
    override fun onTransitionResume(transition: Transition) {}
    override fun onTransitionPause(transition: Transition) {}
    override fun onTransitionCancel(transition: Transition) {}
    override fun onTransitionStart(transition: Transition) {}
}