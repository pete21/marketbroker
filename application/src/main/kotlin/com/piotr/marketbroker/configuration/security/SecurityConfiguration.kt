package com.piotr.marketbroker.configuration.security

import com.piotr.marketbroker.configuration.security.properties.SecurityProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@ConditionalOnProperty(
    prefix = "com.piotr.marketbroker.security",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
class SecurityConfiguration(private val securityProperties: SecurityProperties)
{

    @Bean
    fun filterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http.cors { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                securityProperties.ignoredUrls.map { url-> it.requestMatchers(url).permitAll() }
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { resourceServer ->
                resourceServer.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
            }
        return http.build()
    }

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(KeycloakRealmRoleConverter())
        return jwtAuthenticationConverter
    }
}
