package com.piotr.marketbroker.configuration.tracing

import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TracingConfig {

    @Bean
    fun responseTraceInjectorFilter(
        tracer: Tracer,
        propagator: Propagator
    ): ResponseTraceInjectorFilter? =
        ResponseTraceInjectorFilter(tracer, propagator)

}
