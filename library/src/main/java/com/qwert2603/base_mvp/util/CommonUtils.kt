package com.qwert2603.base_mvp.util

fun Long.abs() = Math.abs(this)

fun String.startWith(s: String) = s + this

fun Int.toSpacedString() = this.toLong().toSpacedString()

fun Long.toSpacedString(replaceInfinity: Boolean = false) =
        if (replaceInfinity && this.abs() == Long.MAX_VALUE)
            "∞".startWith((if (this >= 0) "" else "-"))
        else abs()
                .toString()
                .reversed()
                .mapIndexed { i, c -> if (i % 3 == 0 && i > 0) "$c " else "$c" }
                .reversed()
                .reduce(String::plus)
                .startWith((if (this >= 0) "" else "-"))

fun String.filterSpaces() = filter { it != ' ' }

fun <T> List<T>.reduceToString() = if (isEmpty()) "" else this.map { it.toString() }.reduce { c1, c2 -> "$c1$c2" }

fun String.hashCodeLong()
        = filterIndexed { index, _ -> index % 2 == 0 }.hashCode().toLong() +
        filterIndexed { index, _ -> index % 2 == 1 }.hashCode().toLong() shl Integer.SIZE

