package com.qwert2603.base_mvp.util

import android.util.Log

object LogUtils {

    const val APP_TAG = "AASSDD"
    const val ERROR_MSG = "ERROR!!!"

    fun d(s: String) {
        d(APP_TAG, s)
    }

    fun d(tag: String, s: String) {
        Log.d(tag, s)
    }

    @JvmOverloads
    fun e(s: String = ERROR_MSG, t: Throwable? = null) {
        Log.e(APP_TAG, s, t)
    }

    fun printCurrentStack() {
        Log.v(APP_TAG, "", Exception())
    }

}
