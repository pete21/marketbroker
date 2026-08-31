package com.piotr.marketbroker.configuration.questdb

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "com.piotr.questdb")
data class QuestDbConfigurationProperties(
    val url: String,
    val pgHost: String,
    val pgPort: Int,
    val pgUser: String,
    val pgPassword: String,
)
