package com.piotr.marketbroker.configuration.feign

import com.piotr.marketbroker.common.logger
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.annotation.Bean

class Td365FeignConfiguration {

    private val log by logger()

    @Bean
    fun requestInterceptor(
    ): RequestInterceptor {
        return RequestInterceptor { _ : RequestTemplate ->
            run {
//                val token = getHubspotToken(properties)
//                log.trace("Adding token to HS request")
//                requestTemplate.header("Authorization", "Bearer $token")
            }
        }
    }

//    private fun getHubspotToken(properties: HubspotProperties): String {
//        return properties.token
//    }
}
