package com.qwert2603.base_mvp.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.ViewAnimationUtils
import com.qwert2603.base_mvp.BuildConfig
import com.qwert2603.base_mvp.R

open class CircularRevealDialogFragment : DialogFragment() {

    companion object {
        const val START_POSITION_X = BuildConfig.APPLICATION_ID + "START_POSITION_X"
        const val START_POSITION_Y = BuildConfig.APPLICATION_ID + "START_POSITION_Y"
        const val START_ANIMATION_SHOWN = BuildConfig.APPLICATION_ID + "START_ANIMATION_SHOWN"
    }

    open protected val animatorDuration = 500L

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        LogUtils.d("CircularRevealDialogFragment onStart")
        if (arguments.getBoolean(START_ANIMATION_SHOWN, false)) return
        val startX = arguments.getInt(START_POSITION_X, -1)
        val startY = arguments.getInt(START_POSITION_Y, -1)
        LogUtils.d("CircularRevealDialogFragment onStart $startX $startY")
        if (startX == -1 || startY == -1) return
        runOnLollipopOrHigher {
            val decorView = dialog.window.decorView
            decorView.setOnPreDrawAction {
                val locationOnScreen = arrayOf(0, 0).toIntArray()
                decorView.getLocationOnScreen(locationOnScreen)
                val screenStartX = startX - locationOnScreen[0]
                val screenStartY = startY - locationOnScreen[1]
                arguments.putInt(START_POSITION_X, screenStartX)
                arguments.putInt(START_POSITION_Y, screenStartY)
                val endRadius = Math.hypot(
                        resources.displayMetrics.widthPixels.toDouble(),
                        resources.displayMetrics.heightPixels.toDouble()
                ).toFloat()
                arguments.putBoolean(START_ANIMATION_SHOWN, true)
                LogUtils.d("CircularRevealDialogFragment onStart createCircularReveal $endRadius")
                ViewAnimationUtils.createCircularReveal(decorView, screenStartX, screenStartY, resources.getDimension(R.dimen.circularReveal_minRadius), endRadius)
                        .setDuration(animatorDuration)
                        .start()
            }
        }
    }

    @SuppressLint("NewApi")
    protected fun runExitAnimation() {
        LogUtils.d("CircularRevealDialogFragment runExitAnimation")
        val alertDialog = dialog as? AlertDialog
        if (alertDialog != null) {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {}
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {}
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {}
        }
        if (!arguments.getBoolean(START_ANIMATION_SHOWN, false)) return
        runOnLollipopOrHigher {
            val decorView = dialog.window.decorView
            val startX = arguments.getInt(START_POSITION_X, -1)
            val startY = arguments.getInt(START_POSITION_Y, -1)
            val startRadius = Math.hypot(
                    resources.displayMetrics.widthPixels.toDouble(),
                    resources.displayMetrics.heightPixels.toDouble()
            ).toFloat()
            LogUtils.d("CircularRevealDialogFragment createCircularReveal")
            ViewAnimationUtils.createCircularReveal(decorView, startX, startY, startRadius, resources.getDimension(R.dimen.circularReveal_minRadius))
                    .setDuration(animatorDuration)
                    .also {
                        it.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                LogUtils.d("CircularRevealDialogFragment onAnimationEnd")
                                dismiss()
                            }
                        })
                    }
                    .start()
        }
    }
}