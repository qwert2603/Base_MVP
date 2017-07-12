package com.qwert2603.base_mvp.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.KeyEvent
import android.view.ViewAnimationUtils
import com.qwert2603.base_mvp.BuildConfig
import com.qwert2603.base_mvp.R

open class CircularRevealDialogFragment : DialogFragment() {

    companion object {
        const val START_POSITION_X = BuildConfig.APPLICATION_ID + "START_POSITION_X"
        const val START_POSITION_Y = BuildConfig.APPLICATION_ID + "START_POSITION_Y"
        const val START_ANIMATION_SHOWN = BuildConfig.APPLICATION_ID + "START_ANIMATION_SHOWN"
        const val WAS_RECREATED = BuildConfig.APPLICATION_ID + "WAS_RECREATED"
    }

    open protected val animatorDuration = 300L

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
                        decorView.width.toDouble(),
                        decorView.height.toDouble()
                ).toFloat()
                arguments.putBoolean(START_ANIMATION_SHOWN, true)
                LogUtils.d("CircularRevealDialogFragment onStart createCircularReveal $endRadius")
                decorView.animate().setStartDelay(0).setDuration(animatorDuration / 2).alpha(1f)
                ViewAnimationUtils.createCircularReveal(decorView, screenStartX, screenStartY, resources.getDimension(R.dimen.circularReveal_minRadius), endRadius)
                        .setDuration(animatorDuration)
                        .start()
            }
        }
    }

    @SuppressLint("NewApi")
    protected fun runExitAnimation() {
        LogUtils.d("CircularRevealDialogFragment runExitAnimation $dialog")

        val alertDialog = dialog as? android.app.AlertDialog
        alertDialog?.apply {
            getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {}
            getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {}
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {}
        }

        val alertDialogSupport = dialog as? android.support.v7.app.AlertDialog
        alertDialog?.apply {
            getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {}
            getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {}
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {}
        }

        if (!arguments.getBoolean(START_ANIMATION_SHOWN, false)) return

        val decorView = alertDialog?.window?.decorView ?: alertDialogSupport?.window?.decorView ?: return

        runOnLollipopOrHigher {
            val wasRecreated = arguments.getBoolean(WAS_RECREATED, false)
            val startX = if (wasRecreated) decorView.getCenterX() else arguments.getInt(START_POSITION_X)
            val startY = if (wasRecreated) decorView.getCenterY() else arguments.getInt(START_POSITION_Y)
            val startRadius = Math.hypot(
                    decorView.width.toDouble(),
                    decorView.height.toDouble()
            ).toFloat()
            LogUtils.d("CircularRevealDialogFragment createCircularReveal")
            decorView.animate().setStartDelay(animatorDuration / (if (wasRecreated) 3 else 2)).setDuration(animatorDuration / 2).alpha(0f)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.d("CircularRevealDialogFragment onSaveInstanceState")
        arguments.putBoolean(WAS_RECREATED, true)
    }

    /**
     * @return true if call [runExitAnimation].
     */
    open protected fun onButtonClick(which: Int): Boolean = true

    protected fun android.app.AlertDialog.configAlertDialogForRevealAnimation(): android.app.AlertDialog {
        setOnShowListener {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (onButtonClick(DialogInterface.BUTTON_POSITIVE)) runExitAnimation()
            }
            getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                if (onButtonClick(DialogInterface.BUTTON_NEUTRAL)) runExitAnimation()
            }
            getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                if (onButtonClick(DialogInterface.BUTTON_NEGATIVE)) runExitAnimation()
            }
        }
        return this
    }

    protected fun android.support.v7.app.AlertDialog.configAlertDialogForRevealAnimation(): android.support.v7.app.AlertDialog {
        this.configDialogForRevealAnimation()
        setOnShowListener {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (onButtonClick(DialogInterface.BUTTON_POSITIVE)) runExitAnimation()
            }
            getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                if (onButtonClick(DialogInterface.BUTTON_NEUTRAL)) runExitAnimation()
            }
            getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                if (onButtonClick(DialogInterface.BUTTON_NEGATIVE)) runExitAnimation()
            }
        }
        return this
    }

    private fun Dialog.configDialogForRevealAnimation(): Dialog {
        setCanceledOnTouchOutside(false)
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                runExitAnimation()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return this
    }
}