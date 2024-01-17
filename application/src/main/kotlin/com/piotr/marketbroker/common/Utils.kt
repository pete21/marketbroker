package com.piotr.marketbroker.common

import java.util.Optional

fun <T> Optional<T>.unwrap(): T? = orElse(null)
