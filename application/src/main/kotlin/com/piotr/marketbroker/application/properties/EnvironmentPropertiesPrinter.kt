package com.piotr.marketbroker.application.properties

import com.piotr.marketbroker.common.logger
import jakarta.annotation.PostConstruct
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


@Component
class EnvironmentPropertiesPrinter(private val env: Environment) {

    private val log by logger()

    @PostConstruct
    fun logApplicationProperties() {
        val activeProfiles = env.activeProfiles.joinToString { "," }

        log.info("************************* PROPERTIES(ENVIRONMENT) ******************************")
        log.info("{}={}", "app.name", env.getProperty("app.name"))
        log.info("{}={}", "app.description", env.getProperty("app.description"))
        log.info("{}={}", "spring.kafka.bootstrap-servers", env.getProperty("spring.kafka.bootstrap-servers"))
        log.info("{}={}", "com.piotr.kafka-connect.url", env.getProperty("com.piotr.kafka-connect.url"))
        log.info("{}={}", "activeProfiles", activeProfiles)
        log.info("******************************************************************************")
    }

}
