package com.qwert2603.base_mvp.base

import com.qwert2603.base_mvp.model.IdentifiableLong

class OneShotFlag<T>(val data: T? = null) {
    private var readId: Long? = null

    fun getFlag(vsId: Long) =
            if (readId == null) true.also { readId = vsId }
            else readId == vsId

    fun cancelIfNotUsed() {
        readId = IdentifiableLong.NO_ID
    }

    companion object {
        fun <T> createCancelled() = OneShotFlag<T>().also { it.cancelIfNotUsed() }
    }
}

open class ViewState_ID {
    val viewStateId: Long = getNextId()

    companion object {
        private var lastId = 1L
        fun getNextId() = lastId++
    }
}
