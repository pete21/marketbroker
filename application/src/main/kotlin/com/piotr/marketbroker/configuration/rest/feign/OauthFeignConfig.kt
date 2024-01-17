package com.piotr.marketbroker.configuration.rest.feign

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class OauthFeignConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val oAuth2AuthorizedClientService: OAuth2AuthorizedClientService
) {

    companion object {
        const val KC_REGISTRATION_ID = "keycloak"
    }

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate: RequestTemplate ->
            requestTemplate.header(
                "Authorization",
                "Bearer " + getClientCredentialsFeignManager().getAccessToken()
            )
        }
    }

    private fun getClientCredentialsFeignManager(): OAuthClientCredentialsFeignManager {
        val clientRegistration = clientRegistrationRepository.findByRegistrationId(KC_REGISTRATION_ID)

        return OAuthClientCredentialsFeignManager(
            AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                oAuth2AuthorizedClientService
            ),
            clientRegistration
        )
    }
}

class OAuthClientCredentialsFeignManager(
    private val oAuth2AuthorizedClientManager: OAuth2AuthorizedClientManager,
    private val clientRegistration: ClientRegistration
) {
    fun getAccessToken(): String? {

        val request: OAuth2AuthorizeRequest = OAuth2AuthorizeRequest
            .withClientRegistrationId(clientRegistration.registrationId)
            .principal(clientRegistration.clientName)
            .build()

        val client = oAuth2AuthorizedClientManager.authorize(request) ?: throw CannotRetrieveAccessTokenFromKeycloak(
            clientRegistration.clientName
        )

        return client.accessToken.tokenValue
    }
}
