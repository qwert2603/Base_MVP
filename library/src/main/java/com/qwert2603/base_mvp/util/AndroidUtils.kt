package com.qwert2603.base_mvp.util

import android.os.Build

fun runOnLollipopOrHigher(action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        action()
    }
}

