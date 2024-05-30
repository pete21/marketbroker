package com.piotr.marketbroker.application.properties

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


@Component
class EnvironmentPropertiesPrinter(private val env: Environment) {

    @PostConstruct
    fun logApplicationProperties() {
        val activeProfiles = env.activeProfiles.joinToString { "," }

        LOGGER.info("************************* PROPERTIES(ENVIRONMENT) ******************************")
        LOGGER.info("{}={}", "app.name", env.getProperty("app.name"))
        LOGGER.info("{}={}", "app.description", env.getProperty("app.description"))
        LOGGER.info("{}={}", "spring.kafka.bootstrap-servers", env.getProperty("spring.kafka.bootstrap-servers"))
        LOGGER.info("{}={}", "com.piotr.kafka-connect.url", env.getProperty("com.piotr.kafka-connect.url"))
        LOGGER.info("{}={}", "activeProfiles", activeProfiles)
        LOGGER.info("******************************************************************************")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EnvironmentPropertiesPrinter::class.java)
    }
}
