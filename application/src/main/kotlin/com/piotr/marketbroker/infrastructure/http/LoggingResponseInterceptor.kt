package com.piotr.marketbroker.infrastructure.http

import com.piotr.marketbroker.common.logger
import org.apache.hc.core5.http.EntityDetails
import org.apache.hc.core5.http.HttpException
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.HttpResponseInterceptor
import org.apache.hc.core5.http.protocol.HttpContext
import java.io.IOException

class LoggingResponseInterceptor : HttpResponseInterceptor {

    private val log by logger()

    @Throws(HttpException::class, IOException::class)
    override fun process(response: HttpResponse?, p1: EntityDetails?, context: HttpContext?) {
        log.info(buildResponse(response!!) +
            buildHeaders(response.headers) +
            buildResponseEntity(response, p1))
    }

}
