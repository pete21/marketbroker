package com.piotr.marketbroker.configuration.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
@ConditionalOnProperty(
    prefix = "com.piotr.kafka.producer",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class KafkaConfiguration(
    @Value(value = "\${spring.kafka.bootstrap-servers}") private val bootstrapAddress: String
) {

    @Bean
    fun createKafkaAdmin(): KafkaAdmin {
        val configs = mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress)

        return KafkaAdmin(configs)
    }
}
