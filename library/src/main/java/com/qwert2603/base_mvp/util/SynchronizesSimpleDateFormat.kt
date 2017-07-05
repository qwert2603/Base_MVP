package com.qwert2603.base_mvp.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SynchronizesSimpleDateFormat(pattern: String, locale: Locale) : SimpleDateFormat(pattern, locale) {

    private val lock = Any()

    @Throws(ParseException::class)
    override fun parse(source: String): Date = synchronized(lock, { super.parse(source) })

    fun formatDate(date: Date): String = synchronized(lock, { super.format(date) })
}