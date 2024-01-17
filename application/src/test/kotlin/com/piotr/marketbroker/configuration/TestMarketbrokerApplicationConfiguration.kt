package com.piotr.marketbroker.configuration

import io.micrometer.tracing.propagation.Propagator
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestMarketbrokerApplicationConfiguration {

    @Bean
    @Primary
    fun createTestMicrometerPropagator(): Propagator  {
        return Propagator.NOOP
    }
}