package com.qwert2603.base_mvp.util

import android.content.res.Resources
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.support.transition.TransitionManager
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import com.qwert2603.base_mvp.R
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
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

fun TextView.textConsumer(): Consumer<in String> = Consumer { text = it }

fun TextView.textColorConsumer(): Consumer<in Int> = Consumer { setTextColor(it) }

fun View.enabledConsumer(): Consumer<Boolean> = Consumer { isEnabled = it }

fun View.visibilityConsumer(): Consumer<Boolean> = Consumer {
    TransitionManager.beginDelayedTransition(parent as ViewGroup)
    visibility = if (it) View.VISIBLE else View.GONE
}

fun EditText.textChangesObservable(): Observable<String> = Observable.create { observableEmitter: ObservableEmitter<String> ->
    val tw = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = observableEmitter.onNext(s.toString())
    }
    observableEmitter.setCancellable { removeTextChangedListener(tw) }
    addTextChangedListener(tw)
    observableEmitter.onNext(text.toString())
}

fun EditText.textChangesLongObservable(): Observable<Long> = textChangesObservable()
        .emptyToZero()
        .map(String::filterSpaces)
        .map(String::toLong)
        .distinctUntilChanged()
        .doOnNext {
            if (!text.isEmpty()) {
                setText(it.toSpacedString())
                setSelection(text.length)
            }
        }

fun View.clickObservable(): Observable<Any> = Observable.create { observableEmitter: ObservableEmitter<Any> ->
    setOnClickListener { observableEmitter.onNext(Any()) }
    observableEmitter.setCancellable { setOnClickListener(null) }
}

fun Consumer<in String>.toFormattedString(resources: Resources, @StringRes stringRes: Int): Consumer<Long>
        = Consumer { accept(resources.getString(stringRes, it.toSpacedString(replaceInfinity = true))) }

fun Consumer<in Int>.toErrorResConsumer(@StringRes errorRes: Int): Consumer<Boolean>
        = Consumer { accept(if (it) errorRes else 0) }

fun Consumer<in Int>.toErrorColor(resources: Resources): Consumer<Boolean>
        = Consumer { accept(ResourcesCompat.getColor(resources, if (it) R.color.error else android.R.color.black, null)) }

fun AutoCompleteTextView.suggestionsConsumer(): Consumer<List<String>> = Consumer {
    val s = text.toString()
    if (it.singleOrNull() == s) {
        setText(s)
        setSelection(s.length)
    }
    if (it.isNotEmpty()) {
        setAdapter(SuggestionAdapter(context, s, it))
        showDropDown()
    } else {
        dismissDropDown()
    }
}