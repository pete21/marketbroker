package com.piotr.marketbroker.configuration

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@EnableAutoConfiguration
@Configuration
@ConfigurationPropertiesScan(basePackages = ["com.piotr.marketbroker.configuration"])
class MarketbrokerConfiguration
