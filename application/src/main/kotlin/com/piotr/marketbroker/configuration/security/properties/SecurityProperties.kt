package com.piotr.marketbroker.configuration.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "com.piotr.marketbroker.security")
data class SecurityProperties(
    val ignoredUrls: List<String> = listOf(),
    val enabled: Boolean = true,
    val validateEmployerId: Boolean = false,
    val openidConnectConfigurationUrl: String? = null,
    val openidConnectUrl: String? = null,
    val logoutUrl: String? = null
)
