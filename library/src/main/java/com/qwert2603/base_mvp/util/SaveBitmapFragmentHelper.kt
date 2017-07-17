package com.qwert2603.base_mvp.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment

class SaveBitmapFragmentHelper(val fragment: Fragment) {

    private var prevBackground: Drawable? = null
    private var everPaused = false

    fun onPause() {
        val view = fragment.view.let { it ?: return }
        if (view.width <= 0 || view.height <= 0) return
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.width, view.height)
        view.draw(canvas)
        prevBackground = view.background
        everPaused = true
        view.background = BitmapDrawable(fragment.resources, bitmap)
    }

    fun onResume() {
        if (everPaused) fragment.view?.background = prevBackground
    }
}