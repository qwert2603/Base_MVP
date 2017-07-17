package com.qwert2603.base_mvp.search

import com.qwert2603.base_mvp.util.filterList
import io.reactivex.Single

class SearchHelper<T>(
        private val searchFromServer: (String) -> Single<List<T>>,
        private val nameResult: (T) -> String
) {
    //todo: use BehaviorSubject.
    @Volatile private var cache = emptyList<T>()

    /** empty query means "find all". */
    fun search(query: String, forceFromServer: Boolean = false): Single<List<T>> {
        val q = query.toLowerCase()
        if (forceFromServer) return searchFromServer(query).sortResults(query)
        val serverResults = searchFromServer("").doOnSuccess { cache = it }
        if (q.isBlank()) return serverResults.sortResults(query)
        return Single.just(cache)
                .filterResults(q)
                .flatMap {
                    if (it.isNotEmpty()) Single.just(it)
                    else serverResults.filterResults(q)
                }
                .sortResults(query)
    }

    fun clearCache() {
        cache = emptyList()
    }

    fun addResultToCache(t: T) {
        cache += t
    }

    fun Single<List<T>>.filterResults(q: String) = filterList { nameResult(it).toLowerCase().contains(q) }
    fun Single<List<T>>.sortResults(query: String): Single<List<T>> = map { it.sortSearchResults(nameResult, query) }
}

fun <T> List<T>.sortSearchResults(toString: (T) -> String, search: String) =
        if (search.isBlank()) sortedBy(toString)
        else sortedBy(toString).sortedBy { toString(it).toLowerCase().indexOf(search.toLowerCase()).let { if (it >= 0) it else Int.MAX_VALUE } }