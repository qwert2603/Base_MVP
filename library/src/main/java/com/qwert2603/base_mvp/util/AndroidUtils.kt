package com.qwert2603.base_mvp.util

import android.os.Build
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