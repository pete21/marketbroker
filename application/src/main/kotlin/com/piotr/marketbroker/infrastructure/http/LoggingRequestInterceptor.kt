package com.piotr.marketbroker.infrastructure.http

import mu.KotlinLogging
import org.apache.hc.core5.http.EntityDetails
import org.apache.hc.core5.http.HttpRequest
import org.apache.hc.core5.http.HttpRequestInterceptor
import org.apache.hc.core5.http.protocol.HttpContext
import java.io.IOException
import java.util.*

private val log = KotlinLogging.logger(LoggingRequestInterceptor::class.toString())

class LoggingRequestInterceptor : HttpRequestInterceptor {
    @Throws(IOException::class)
    override fun process(request: HttpRequest?, p1: EntityDetails?, context: HttpContext?) {
        log.info(buildRequest(request!!, context!!) +
            buildHeaders(request.headers) +
            buildRequestEntity(request, p1))
    }

}
