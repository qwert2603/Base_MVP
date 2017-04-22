package com.qwert2603.base_mvp.model

class CounterMap<T> : HashMap<T, Int>() {

    fun putOne(key: T) = put(key, 1)

    override fun put(key: T, value: Int): Int = (this[key] + value).also { super.put(key, it) }

    override fun get(key: T): Int = super.get(key) ?: 0

    fun toSortedList() = keys.toList().sortedByDescending { this[it] }
}