package com.qwert2603.base_mvp.search

import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util.mapList
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
        val suggestionsShowConsumer: Consumer<State>,
        val setSuggestionToModel: (T?) -> Unit
) {
    private data class SearchParams(
            val cancelPrevious: Boolean,
            val showAll: Boolean,
            val search: String
    )

    sealed class State {
        object Cancel : State()
        object Loading : State()
        object NothingFound : State()
        object Error : State()
        data class Suggestions(val suggestions: List<String>) : State()
    }

    private var suggestions = emptyList<T>()

    fun subscribe(): Disposable {
        return Observable.merge(
                textChanges.map { SearchParams(false, false, it) },
                showAllClicks.map { SearchParams(false, true, "") },
                cancelPrevious.map { SearchParams(true, false, "") }
        )
                .filter { filter() }
                .switchMap { (cancelPrevious, showAll, search) ->
                    LogUtils.d({ "AutocompleteHelper $cancelPrevious $showAll $search" })
                    val cancel = Observable.just(State.Cancel)
                    if (cancelPrevious) return@switchMap cancel
                    if (!showAll) {
                        if (search.isBlank()) return@switchMap cancel
                        suggestions
                                .firstOrNull { nameSuggestionObject(it) == search }
                                ?.let {
                                    setSuggestionToModel(it)
                                    return@switchMap cancel
                                }
                        setSuggestionToModel(null)
                    }
                    return@switchMap (if (showAll) allSuggestionsSource() else suggestionsSource(search))
                            .doOnSuccess { suggestions = it }
                            .flatMap {
                                it.firstOrNull { nameSuggestionObject(it) == search }
                                        ?.let {
                                            setSuggestionToModel(it)
                                            return@flatMap Single.just(State.Cancel)
                                        }
                                return@flatMap Single.just(it)
                                        .mapList { nameSuggestionObject(it) }
                                        .map { if (it.isEmpty()) State.NothingFound else State.Suggestions(it) }
                                        .cast(State::class.java)
                            }
                            .toObservable()
                            .onErrorReturn {
                                LogUtils.e("Error loading suggestions!", it)
                                State.Error
                            }
                            .startWith(State.Loading)
                }
                .subscribe(suggestionsShowConsumer)
    }
}
