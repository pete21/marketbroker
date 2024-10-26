package com.piotr.marketbroker.configuration.kafkaconnect

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "com.piotr.kafkaconnect")
data class KafkaConnectConfigurationProperties (

    val url: String,

    val questdbHost: String
)