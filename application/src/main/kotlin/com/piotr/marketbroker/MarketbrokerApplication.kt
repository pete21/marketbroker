package com.piotr.marketbroker

import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.kafkaconnect.KafkaConnectConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(TD365ConfigurationProperties::class, KafkaConnectConfigurationProperties::class)
class MarketbrokerApplication {

    private val log by logger()
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<MarketbrokerApplication>(*args) {
                applicationStartup = BufferingApplicationStartup(2048)
            }
        }
    }

    @Async
    @EventListener
    fun onReady(event: ApplicationReadyEvent) {
        log.info("Application is ready: $event")
    }
}
