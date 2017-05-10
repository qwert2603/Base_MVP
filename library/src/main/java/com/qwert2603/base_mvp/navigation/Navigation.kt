package com.qwert2603.base_mvp.navigation

import android.view.View
import android.widget.EditText

interface Navigation {
    fun modifyBackStack(newBackStack: List<BackStackItem>, sharedElements: List<View> = emptyList())
    fun navigateTo(backStackItem: BackStackItem, delay: Boolean = true, sharedElements: List<View> = emptyList())
    fun removeBackStackItem(backStackItem: BackStackItem, sharedElements: List<View> = emptyList())

    fun hideKeyboard(removeFocus: Boolean = true)
    fun showKeyboard(editText: EditText)
    fun blockUI(millis: Long, actionOnEnd: (() -> Unit)? = null)

    fun onFragmentResumed(fragment: BackStackFragment<*, *>)
    fun onFragmentPaused(fragment: BackStackFragment<*, *>)
}