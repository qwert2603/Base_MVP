package com.qwert2603.base_mvp.navigation

import android.widget.EditText

interface Navigation {
    fun modifyBackStack(newBackStack: List<BackStackItem>)
    fun navigateTo(backStackItem: BackStackItem, delay: Boolean = true)
    fun removeBackStackItem(backStackItem: BackStackItem)

    fun hideKeyboard(removeFocus: Boolean = true)
    fun showKeyboard(editText: EditText)
    fun blockUI(millis: Long, actionOnEnd: (() -> Unit)? = null)

    fun onFragmentResumed(fragment: BackStackFragment<*, *>)
    fun onFragmentPaused(fragment: BackStackFragment<*, *>)
}