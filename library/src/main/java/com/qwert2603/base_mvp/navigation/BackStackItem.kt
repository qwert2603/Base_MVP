package com.qwert2603.base_mvp.navigation

import android.os.Bundle
import android.view.View
import java.io.Serializable

abstract class BackStackItem : Serializable {

    companion object {
        const val BACK_STACK_ITEM_KEY = "com.qwert2603.base_mvp.navigation.BACK_STACK_ITEM_KEY"
    }

    abstract val tag: String

    /**
     * Use enter/exit transitions or not.
     */
    open val withTransitions = true

    /**
     * If true, this BackStackItem doesn't change toolbar icon & title.
     * Also when [BaseMainActivity.goBack] this BackStackItem will be deleted together with prev BackStackItem in which [asNested] == true.
     */
    open val asNested = false

    fun createFragment() = newInstance().apply {
        val args: Bundle = getArguments() ?: Bundle()
        args.putSerializable(BACK_STACK_ITEM_KEY, this@BackStackItem)
        setArguments(args)
    }

    abstract protected fun newInstance(): BackStackFragment<*, *>
}

class BackStackChange(
        val from: List<BackStackItem>,
        val to: List<BackStackItem>,
        val sharedElements: List<View>
)