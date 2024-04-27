package com.piotr.marketbroker.infrastructure.http

import mu.KotlinLogging
import org.apache.hc.core5.http.EntityDetails
import org.apache.hc.core5.http.HttpException
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.HttpResponseInterceptor
import org.apache.hc.core5.http.protocol.HttpContext
import java.io.IOException

private val log = KotlinLogging.logger(LoggingResponseInterceptor::class.toString())

class LoggingResponseInterceptor : HttpResponseInterceptor {
    @Throws(HttpException::class, IOException::class)
    override fun process(response: HttpResponse?, p1: EntityDetails?, context: HttpContext?) {
        log.info(buildResponse(response!!) +
            buildHeaders(response.headers) +
            buildResponseEntity(response, p1))
    }

}
