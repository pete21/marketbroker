package com.piotr.marketbroker.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Optional
import kotlin.reflect.full.companionObject

fun <T> Optional<T>.unwrap(): T? = orElse(null)

fun <T> T?.stringify(toString: (T) -> String = { it.toString() }): String =
    if (this == null) "" else toString(this)


fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name) }
}

fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return ofClass.enclosingClass?.takeIf {
        ofClass.enclosingClass.kotlin.companionObject?.java == ofClass
    } ?: ofClass
}
