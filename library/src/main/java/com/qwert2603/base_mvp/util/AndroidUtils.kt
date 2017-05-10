package com.qwert2603.base_mvp.util

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver

fun runOnLollipopOrHigher(action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        action()
    }
}

fun View.setOnDrawAction(action: () -> Unit) {
    viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
        override fun onDraw() {
            viewTreeObserver.removeOnDrawListener(this)
            action()
        }
    })
}