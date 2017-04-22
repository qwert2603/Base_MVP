package com.qwert2603.base_mvp.model

open class InMemoryCache<K, T>(val keySelector: (T) -> K) {
    private val cache = mutableMapOf<K, T>()

    fun put(t: T): Unit {
        cache.put(keySelector(t), t)
    }

    fun get(k: K): T? = cache[k]

    fun clear() = cache.clear()

    override fun toString() = cache.toString()
}

class InMemoryCacheIdentifiableLong<T : IdentifiableLong> : InMemoryCache<Long, T>({ it.id })

class InMemoryCacheIdentifiableString<T : IdentifiableString> : InMemoryCache<String, T>({ it.objectId })