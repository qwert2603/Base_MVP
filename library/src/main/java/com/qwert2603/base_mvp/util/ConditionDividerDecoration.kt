package com.qwert2603.base_mvp.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View

class ConditionDividerDecoration(context: Context, val condition: (RecyclerView, RecyclerView.ViewHolder) -> Boolean) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable

    private val mBounds = Rect()

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        drawVertical(c, parent)
    }

    @SuppressLint("NewApi")
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            if (i == childCount - 1 || condition(parent, parent.findContainingViewHolder(parent.getChildAt(i))!!)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child))
                val top = bottom - mDivider.intrinsicHeight
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(canvas)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val viewHolder = parent.findContainingViewHolder(view)!!
        val adapterPosition = viewHolder.adapterPosition
        if (adapterPosition == state.itemCount - 1 || condition(parent, viewHolder)) {
            outRect.set(0, 0, 0, mDivider.intrinsicHeight)
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}
