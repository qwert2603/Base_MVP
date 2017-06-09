package com.qwert2603.base_mvp.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.qwert2603.base_mvp.base.BaseFragment
import com.qwert2603.base_mvp.base.BasePresenter
import com.qwert2603.base_mvp.base.BaseView
import com.qwert2603.base_mvp.base.BaseViewStateContainer

abstract class SaveBitmapFragment<VS : BaseViewStateContainer, V : BaseView<VS>, P : BasePresenter<V, VS>> : BaseFragment<VS, V, P>() {
    private var prevBackground: Drawable? = null
    private var everPaused = false

    override fun onPause() {
        super.onPause()

        val view = view.let { it ?: return }
        if (view.width <= 0 || view.height <= 0) return
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