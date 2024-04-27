package com.piotr.marketbroker.configuration.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenAPIConfig {
    @Value("\${com.piotr.marketbroker.openapi.dev-url}")
    private val devUrl: String? = null

    @Value("\${com.piotr.marketbroker.openapi.prod-url}")
    private val prodUrl: String? = null

    @Bean
    fun openAPI(): OpenAPI {
        val devServer = Server()
        devServer.url = devUrl
        devServer.description = "Server URL in Development environment"

        val prodServer = Server()
        prodServer.url = prodUrl
        prodServer.description = "Server URL in Production environment"

        val contact = Contact()
        contact.email = "piotr.nazarewicz@gmail.com"
        contact.name = "Piotr"
        contact.url = "https://piotr"

        val mitLicense = License().name("MIT License").url("https://choosealicense.com/licenses/mit/")

        val info = Info()
            .title("Marketbroker API")
            .version("1.0")
            .contact(contact)
            .description("This is Marketbroker API.")
            .termsOfService("https://piotr/terms")
            .license(mitLicense)

        return OpenAPI().info(info).servers(listOf(devServer, prodServer))
    }
}