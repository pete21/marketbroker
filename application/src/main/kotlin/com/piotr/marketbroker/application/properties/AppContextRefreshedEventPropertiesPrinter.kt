package com.piotr.marketbroker.application.properties

import com.piotr.marketbroker.common.logger
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.stereotype.Component


@Component
class AppContextRefreshedEventPropertiesPrinter {

    private val log by logger()

    @EventListener
    fun handleContextRefreshed(event: ContextRefreshedEvent) {
        printAllActiveProperties(event.applicationContext.environment as ConfigurableEnvironment)
        printAllApplicationProperties(event.applicationContext.environment as ConfigurableEnvironment)
    }

    private fun printAllActiveProperties(env: ConfigurableEnvironment) {
        log.info("************************* ALL PROPERTIES(EVENT) ******************************")

        env.propertySources
            .stream()
            .filter { ps: PropertySource<*>? -> ps is MapPropertySource }
            .map<Set<String>> { ps: PropertySource<*> -> (ps as MapPropertySource).source.keys }
            .flatMap { obj: Set<String> -> obj.stream() }
            .distinct()
            .sorted()
            .forEach { key: String? -> log.info("{}={}", key, env.getProperty(key!!)) }

        log.info("******************************************************************************")
    }

    private fun printAllApplicationProperties(env: ConfigurableEnvironment) {
        log.info("************************* APP PROPERTIES(EVENT) ******************************")

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
                log.info("{}={}", key, env.getProperty(key!!)) }

        log.info("******************************************************************************")
    }

}
