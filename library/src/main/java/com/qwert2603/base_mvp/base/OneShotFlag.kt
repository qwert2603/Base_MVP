package com.qwert2603.base_mvp.base

class OneShotFlag {
    private var readId: Long? = null

    fun getFlag(vsId: Long) =
            if (readId == null) true.also { readId = vsId }
            else readId == vsId

    fun cancelIfNotUsed() {
        readId = -1L
    }

    companion object {
        val CANCELLED = OneShotFlag().also { it.cancelIfNotUsed() }
    }
}

open class ViewState_ID {
    val id: Long = getNextId()

    companion object {
        private var lastId = 1L
        fun getNextId() = lastId++
    }
}
