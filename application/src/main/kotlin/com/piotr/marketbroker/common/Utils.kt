package com.piotr.marketbroker.common

import java.util.Optional

fun <T> Optional<T>.unwrap(): T? = orElse(null)

fun <T> T?.stringify(toString: (T) -> String = { it.toString() }): String =
    if (this == null) "" else toString(this)