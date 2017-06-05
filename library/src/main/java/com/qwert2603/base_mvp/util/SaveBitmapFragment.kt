package com.qwert2603.base_mvp.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.base.BaseView
import com.qwert2603.base_mvp.navigation.BackStackFragment

abstract class SaveBitmapFragment<V : BaseView, out P : BasePresenter<*, V>> : BackStackFragment<V, P>() {
    private var prevBackground: Drawable? = null
    private var everPaused = false

    override fun onPause() {
        super.onPause()

        val view = view.let { it ?: return }
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.width, view.height)
        view.draw(canvas)
        prevBackground = view.background
        everPaused = true
        view.background = BitmapDrawable(resources, bitmap)
    }

    override fun onResume() {
        super.onResume()
        if (everPaused) view?.background = prevBackground
    }
}