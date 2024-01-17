package com.piotr.marketbroker

import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties(TD365ConfigurationProperties::class)
@EnableFeignClients
@Suppress("UtilityClassWithPublicConstructor", "SpreadOperator", "MagicNumber")
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
