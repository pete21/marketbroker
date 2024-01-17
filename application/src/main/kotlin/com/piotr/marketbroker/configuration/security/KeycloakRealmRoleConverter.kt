package com.piotr.marketbroker.configuration.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class KeycloakRealmRoleConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    private val jwtGrantedAuthoritiesConverter: JwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): Collection<GrantedAuthority>? {
        val originalAuthorities = jwtGrantedAuthoritiesConverter.convert(jwt)
        val keycloakAuthorities = getKeycloakAuthorities(jwt)
        return originalAuthorities?.plus(keycloakAuthorities ?: emptyList())
    }

    private fun getKeycloakAuthorities(jwt: Jwt): Collection<SimpleGrantedAuthority>? {
        val keycloakClaims = jwt.claims[REALM_ACCESS_CLAIM] as Map<String, List<String>>?
        val keycloakRoles = keycloakClaims?.get(ROLES_CLAIM)
        return keycloakRoles?.map { SimpleGrantedAuthority("$ROLE_PREFIX$it") }
    }

    companion object {
        private const val REALM_ACCESS_CLAIM = "realm_access"
        private const val ROLES_CLAIM = "roles"
        private const val ROLE_PREFIX = "ROLE_"
    }
}
