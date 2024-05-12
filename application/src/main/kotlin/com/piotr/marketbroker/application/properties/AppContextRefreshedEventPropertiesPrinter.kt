package com.piotr.marketbroker.application.properties

import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.stereotype.Component


@Component
class AppContextRefreshedEventPropertiesPrinter {
    @EventListener
    fun handleContextRefreshed(event: ContextRefreshedEvent) {
        printAllActiveProperties(event.applicationContext.environment as ConfigurableEnvironment)
        printAllApplicationProperties(event.applicationContext.environment as ConfigurableEnvironment)
    }

    private fun printAllActiveProperties(env: ConfigurableEnvironment) {
        LOGGER.info("************************* ALL PROPERTIES(EVENT) ******************************")

        env.propertySources
            .stream()
            .filter { ps: PropertySource<*>? -> ps is MapPropertySource }
            .map<Set<String>> { ps: PropertySource<*> -> (ps as MapPropertySource).source.keys }
            .flatMap { obj: Set<String> -> obj.stream() }
            .distinct()
            .sorted()
            .forEach { key: String? ->
                LOGGER.info(
                    "{}={}", key, env.getProperty(
                        key!!
                    )
                )
            }

        LOGGER.info("******************************************************************************")
    }

    private fun printAllApplicationProperties(env: ConfigurableEnvironment) {
        LOGGER.info("************************* APP PROPERTIES(EVENT) ******************************")

        env.propertySources
            .stream()
            .filter { ps: PropertySource<*> ->
                ps is MapPropertySource && ps.getName().contains("application.properties")
            }
            .map<Set<String>> { ps: PropertySource<*> -> (ps as MapPropertySource).source.keys }
            .flatMap { obj: Set<String> -> obj.stream() }
            .distinct()
            .sorted()
            .forEach { key: String? ->
                LOGGER.info(
                    "{}={}", key, env.getProperty(
                        key!!
                    )
                )
            }

        LOGGER.info("******************************************************************************")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AppContextRefreshedEventPropertiesPrinter::class.java)
    }
}