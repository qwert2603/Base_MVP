package com.qwert2603.base_mvp.util

import android.content.res.Resources
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.support.v4.content.res.ResourcesCompat
import com.qwert2603.base_mvp.R
import io.reactivex.functions.Consumer

fun TextInputLayout.errorResConsumer(setErrorEnabled: Boolean = true): Consumer<in Int> = Consumer { errorRes ->
    if (setErrorEnabled) {
        isErrorEnabled = errorRes != 0
    }
    if (errorRes == 0) {
        error = null
    } else {
        error = context.resources.getText(errorRes)
    }
}

fun Consumer<in String>.toFormattedString(resources: Resources, @StringRes stringRes: Int): Consumer<Long>
        = Consumer { accept(resources.getString(stringRes, it.toSpacedString(replaceInfinity = true))) }

fun Consumer<in Int>.toErrorResConsumer(@StringRes errorRes: Int): Consumer<Boolean>
        = Consumer { accept(if (it) errorRes else 0) }

fun Consumer<in Int>.toErrorColor(resources: Resources): Consumer<Boolean>
        = Consumer { accept(ResourcesCompat.getColor(resources, if (it) R.color.error else android.R.color.black, null)) }
