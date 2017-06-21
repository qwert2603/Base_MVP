package com.qwert2603.base_mvp.base

class OneShotFlag {
    private var readId: Long? = null

    fun getFlag(vsId: Long) =
            if (readId == null) true.also { readId = vsId }
            else readId == vsId

    companion object {
        val USED = OneShotFlag().also { it.readId = -1L }
    }
}

open class ViewState_ID {
    val id: Long = getNextId()

    companion object {
        private var lastId = 1L
        fun getNextId() = lastId++
    }
}
