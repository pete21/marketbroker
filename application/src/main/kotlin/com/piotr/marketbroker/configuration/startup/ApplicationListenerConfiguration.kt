package com.piotr.marketbroker.configuration.startup

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class ApplicationListenerConfiguration(private val environment: Environment) {

    @Bean
    fun applicationListener(): ApplicationListener {
        return ApplicationListener(environment)
    }
}
