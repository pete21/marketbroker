package com.piotr.marketbroker.infrastructure.http

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy
import org.apache.hc.core5.http.HeaderElements
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.message.BasicHeaderElementIterator
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.hc.core5.util.TimeValue

class CustomConnectionKeepAliveStrategy : ConnectionKeepAliveStrategy {

    // Default fallback to 5 seconds if the server doesn't specify
    private val defaultTimeout = TimeValue.ofSeconds(5)

    override fun getKeepAliveDuration(response: HttpResponse, context: HttpContext): TimeValue {
        // Iterate through all "Keep-Alive" headers in the response
        val headerIterator = BasicHeaderElementIterator(
            response.headerIterator(HeaderElements.KEEP_ALIVE)
        )
        while (headerIterator.hasNext()) {
            val element = headerIterator.next()
            val name = element.name
            val value = element.value

            // Look for the "timeout" parameter inside the Keep-Alive header
            if (value != null && name.equals("timeout", ignoreCase = true)) {
                return try {
                    TimeValue.ofSeconds(value.toLong())
                } catch (e: NumberFormatException) {
                    defaultTimeout
                }
            }
        }

        return defaultTimeout
    }
}


//val myStrategy: ConnectionKeepAliveStrategy = object : ConnectionKeepAliveStrategy() {
//    public override fun getKeepAliveDuration(response: HttpResponse?, context: HttpContext?): TimeValue {
//        Args.notNull(response, "HTTP response")
//        val it: MutableIterator<HeaderElement> = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE)
//        val he: HeaderElement = it.next()
//        val param: String = he.getName()
//        val value: String? = he.getValue()
//        if (value != null && param.equals("timeout", ignoreCase = true)) {
//            try {
//                return TimeValue.ofSeconds(value.toLong())
//            } catch (ignore: NumberFormatException) {
//            }
//        }
//        return TimeValue.ofSeconds(5)
//    }
//}

