package com.piotr.marketbroker.configuration.tracing

import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ResponseTraceInjectorFilter(private val tracer: Tracer, private val propagator: Propagator) :
    OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        propagator.inject(
            tracer.currentTraceContext()?.context(), response
        ) { carrier, key, value -> carrier?.addHeader(key, value) }
        filterChain.doFilter(request, response)
    }

}
