package com.piotr.marketbroker.infrastructure.feign.td365

import com.piotr.marketbroker.configuration.feign.Td365FeignConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Profile

@FeignClient(
    name = "td365",
    configuration = [Td365FeignConfiguration::class]
)
@Profile("!integration-test")
@Suppress("EmptyClassBlock")
interface FeignTd365Client : Td365Client

interface Td365Client : RestApi
