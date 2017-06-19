package com.qwert2603.base_mvp.util

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function3

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

class Consumer_2<T>(val c1: Consumer<T>, val c2: Consumer<T>) : Consumer<T> {
    override fun accept(t: T) {
        c1.accept(t)
        c2.accept(t)
    }
}

fun Completable._subscribe(onFinish: (Throwable?) -> Unit): Disposable = subscribe({ onFinish.invoke(null) }, onFinish)

fun Observable<String>.emptyToZero(): Observable<String> = map { if (it == "" || it == ".") "0" else it }

fun Function3<Long, Double, Int, Double>.nonPositiveToZero(): Function3<Long, Double, Int, Double> = Function3 { q1, q2, q3 ->
    if (q1 <= 0L || q2 <= 0.0 || q3 <= 0) 0.0 else this.apply(q1, q2, q3)
}

fun <T, U> Single<List<T>>.mapList(mapper: (T) -> U): Single<List<U>> = this
        .flatMapObservable { Observable.fromIterable(it) }
        .map(mapper)
        .toList()

fun <T> Single<List<T>>.doOnNextList(onNext: (T) -> Unit): Single<List<T>> = this
        .flatMapObservable { Observable.fromIterable(it) }
        .doOnNext(onNext)
        .toList()

fun <T> Single<List<T>>.filterList(filter: (T) -> Boolean): Single<List<T>> = this
        .map { it.filter { filter(it) } }

fun <T> Single<T>.startWith(completable: Completable): Single<T> = completable.toSingleDefault(Any()).flatMap { this }

fun Completable.cacheIfComplete() = CachingCompletable(this)

class CachingCompletable(val originalCompletable: Completable) {
    @Volatile private var completed: Completable? = null
    @Volatile private var cached: Completable = createCached()

    private fun createCached(): Completable = originalCompletable
            .cache()
            .doOnComplete {
                LogUtils.d("CachingCompletable completed = Completable.complete()")
                completed = Completable.complete()
            }
            .doOnError {
                LogUtils.d("CachingCompletable cached = createCached()")
                cached = createCached()
            }

    fun get(): Completable = completed ?: cached
}