package com.qwert2603.base_mvp.widgets

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import com.qwert2603.base_mvp.R

class ForegroundedLinearLayout : LinearLayout {

    private var mForeground: Drawable? = null

    private val mSelfBounds = Rect()
    private val mOverlayBounds = Rect()

    private var mForegroundGravity = Gravity.FILL

    private var mForegroundInPadding = true

    internal var mForegroundBoundsChanged = false

    constructor(context: Context) : super(context)

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundedLinearLayout,
                defStyle, 0)

        mForegroundGravity = a.getInt(R.styleable.ForegroundedLinearLayout_android_foregroundGravity, mForegroundGravity)

        val d = a.getDrawable(R.styleable.ForegroundedLinearLayout_android_foreground)
        if (d != null) {
            foreground = d
        }

        mForegroundInPadding = a.getBoolean(R.styleable.ForegroundedLinearLayout_android_foregroundInsidePadding, true)

        a.recycle()
    }

    override fun getForegroundGravity(): Int {
        return mForegroundGravity
    }

    override fun setForegroundGravity(foregroundGravity: Int) {
        var fg = foregroundGravity
        if (mForegroundGravity != fg) {
            if (fg and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                fg = fg or Gravity.START
            }

            if (fg and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                fg = fg or Gravity.TOP
            }

            mForegroundGravity = fg


            if (mForegroundGravity == Gravity.FILL) {
                val padding = Rect()
                mForeground?.getPadding(padding)
            }

            requestLayout()
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mForeground
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        mForeground?.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mForeground?.isStateful ?: false) {
            mForeground?.state = drawableState
        }
    }

    override fun setForeground(drawable: Drawable?) {
        if (mForeground !== drawable) {
            mForeground?.let { unscheduleDrawable(it) }
            mForeground?.callback = null

            mForeground = drawable

            if (drawable != null) {
                setWillNotDraw(false)
                drawable.callback = this
                if (drawable.isStateful) {
                    drawable.state = drawableState
                }
                if (mForegroundGravity == Gravity.FILL) {
                    val padding = Rect()
                    drawable.getPadding(padding)
                }
            } else {
                setWillNotDraw(true)
            }
            requestLayout()
            invalidate()
        }
    }

    override fun getForeground(): Drawable? {
        return mForeground
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mForegroundBoundsChanged = changed
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mForegroundBoundsChanged = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val foreground = mForeground
        if (foreground != null) {

            if (mForegroundBoundsChanged) {
                mForegroundBoundsChanged = false
                val selfBounds = mSelfBounds
                val overlayBounds = mOverlayBounds

                val w = right - left
                val h = bottom - top

                if (mForegroundInPadding) {
                    selfBounds.set(0, 0, w, h)
                } else {
                    selfBounds.set(paddingLeft, paddingTop,
                            w - paddingRight, h - paddingBottom)
                }

                Gravity.apply(mForegroundGravity, foreground.intrinsicWidth,
                        foreground.intrinsicHeight, selfBounds, overlayBounds)
                foreground.bounds = overlayBounds
            }

            foreground.draw(canvas)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (e.actionMasked == MotionEvent.ACTION_DOWN) {
                mForeground?.setHotspot(e.x, e.y)
            }
        }
        return super.onTouchEvent(e)
    }
}