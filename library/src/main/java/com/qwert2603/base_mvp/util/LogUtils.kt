package com.qwert2603.base_mvp.util

import android.util.Log

object LogUtils {

    const val APP_TAG = "AASSDD"
    const val ERROR_MSG = "ERROR!!!"
    var enableLogging = true

    fun d(msg: String) {
        d(APP_TAG, msg)
    }

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    fun d(msg: () -> String) {
        if (enableLogging) d(APP_TAG, msg)
    }

    fun d(tag: String, msg: () -> String) {
        if (enableLogging) Log.d(tag, msg())
    }

    @JvmOverloads
    fun e(msg: String = ERROR_MSG, t: Throwable? = null) {
        if (enableLogging) Log.e(APP_TAG, "$msg $t", t)
    }

    fun printCurrentStack() {
        if (enableLogging) Log.v(APP_TAG, "", Exception())
    }

}
