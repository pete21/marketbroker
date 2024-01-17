package com.piotr.marketbroker.configuration.td365

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "td365")
@PropertySource("classpath:td365config.properties")
data class TD365ConfigurationProperties(
    val demolink: String,
    val demobaseurl: String,
    val prodlink: String,
    val prodbaseurl: String,
    val demoHeaders: Map<String, String>,
    val prodHeaders: Map<String, String>,
    val demoReferer: String,
    val prodReferer: String,
    val demowebsocketserver: String,
    val prodwebsocketserver: String,
    val authlink: String,
    val accountlink: String,
    val sessionupdateinterval: Int,
    val username: String,
    val password: String
)
