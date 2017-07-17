package com.qwert2603.base_mvp.util

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewAnimator

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ViewAnimator.showIfNotYet(child: Int) {
    if (child != displayedChild) {
        displayedChild = child
    }
}

fun TextView.setTextIfNotYet(text: String) {
    if (text != this.text.toString()) {
        LogUtils.d("TextView.setTextIfNotYet $text")
        this.text = text
    } else {
        LogUtils.d("TextView.setTextIfNotYet text == this.text.toString()")
    }
}

fun View.getCenterX(): Int {
    val locationOnScreen = arrayOf(0, 0).toIntArray()
    getLocationOnScreen(locationOnScreen)
    return locationOnScreen[0] + width / 2
}

fun View.getCenterY(): Int {
    val locationOnScreen = arrayOf(0, 0).toIntArray()
    getLocationOnScreen(locationOnScreen)
    return locationOnScreen[1] + height / 2
}