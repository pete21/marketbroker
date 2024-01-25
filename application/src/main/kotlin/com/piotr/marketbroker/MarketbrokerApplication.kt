package com.piotr.marketbroker

import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource


@SpringBootApplication
@PropertySource("classpath:td365config.properties")
@EnableConfigurationProperties(TD365ConfigurationProperties::class)
class MarketbrokerApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<MarketbrokerApplication>(*args) {
                applicationStartup = BufferingApplicationStartup(2048)
            }
        }
    }
}
