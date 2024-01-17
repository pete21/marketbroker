package com.piotr.marketbroker.configuration.tracing

interface TraceProvider {

    fun traceId(): String?
}
