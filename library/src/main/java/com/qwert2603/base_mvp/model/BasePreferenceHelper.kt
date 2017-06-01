package com.qwert2603.base_mvp.model

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.qwert2603.base_mvp.BaseApplication
import io.reactivex.Single
import javax.inject.Inject

class BasePreferenceHelper {

    class KeyValueHelper<T>(
            val clazz: Class<T>,
            val key: String,
            val preferences: SharedPreferences,
            val gson: Gson,
            val noSavedValueExceptionProvider: () -> Exception = { KeyValueHelper.NoSavedValueException(key) }
    ) {
        private var value: T? = null

        fun load(): Single<T> {
            value?.let { return Single.just(it) }
            return Single.defer {
                preferences.getString(key, null)
                        ?.let { gson.fromJson(it, clazz) }
                        ?.also { value = it }
                        ?.let { Single.just(it) }
                        ?: Single.error(noSavedValueExceptionProvider())
            }
        }

        fun save(t: T) {
            value = t
            preferences.edit()
                    .putString(key, gson.toJson(t))
                    .apply()
        }

        fun remove() {
            value = null
            preferences.edit()
                    .remove(key)
                    .apply()
        }

        class NoSavedValueException(val key: String) : Exception()
    }

    @Inject lateinit var context: Context

    private val preferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private val gson = GsonBuilder().create()

    init {
        BaseApplication.baseDiManager.appContextComponent.inject(this@BasePreferenceHelper)
    }
}

