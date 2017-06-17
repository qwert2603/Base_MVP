package com.qwert2603.base_mvp.util

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class AutocompleteHelper<T>(
        val filter: () -> Boolean,
        val textChanges: Observable<String>,
        val showAllClicks: Observable<Any>,
        val cancelPrevious: Observable<Any>,
        val suggestionsSource: (String) -> Single<List<T>>,
        val allSuggestionsSource: () -> Single<List<T>>,
        val nameSuggestionObject: (T) -> String,
        val suggestionsShowConsumer: Consumer<List<String>>,
        val notifyErrorLoadingSuggestions: () -> Unit,
        val setSuggestionToModel: (T?) -> Unit
) {
    private data class SearchParams(
            val cancelPrevious: Boolean,
            val showAll: Boolean,
            val search: String
    )

    private var suggestions = emptyList<T>()

    fun subscribe(): Disposable {
        return Observable.merge(
                textChanges.map { SearchParams(false, false, it) },
                showAllClicks.map { SearchParams(false, true, "") },
                cancelPrevious.map { SearchParams(true, false, "") }
        )
                .filter { filter() }
                .switchMap { (cancelPrevious, showAll, search) ->
                    LogUtils.d("_AutocompleteHelper $cancelPrevious $showAll $search")
                    if (cancelPrevious) {
                        return@switchMap Observable.just<List<String>>(emptyList())
                    }
                    if (!showAll) {
                        if (search.isBlank()) {
                            return@switchMap Observable.just<List<String>>(emptyList())
                        }
                        suggestions
                                .firstOrNull { nameSuggestionObject(it) == search }
                                ?.let {
                                    setSuggestionToModel(it)
                                    return@switchMap Observable.just<List<String>>(emptyList())
                                }
                        setSuggestionToModel(null)
                    }
                    return@switchMap (if (showAll) allSuggestionsSource() else suggestionsSource(search))
                            .onErrorReturn {
                                LogUtils.e("Error loading suggestions!", it)
                                notifyErrorLoadingSuggestions()
                                emptyList()
                            }
                            .doOnSuccess { suggestions = it }
                            .map {
                                it.firstOrNull { nameSuggestionObject(it) == search }
                                        ?.let {
                                            setSuggestionToModel(it)
                                            return@map emptyList<T>()
                                        }
                                return@map it
                            }
                            .mapList { nameSuggestionObject(it) }
                            .toObservable()
                }
                .subscribe(suggestionsShowConsumer)
    }
}
