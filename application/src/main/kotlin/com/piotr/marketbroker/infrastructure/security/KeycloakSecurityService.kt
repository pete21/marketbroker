package com.piotr.marketbroker.infrastructure.security

import net.minidev.json.JSONArray
import net.minidev.json.JSONObject

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@ConditionalOnProperty(
    prefix = "com.piotr.security.marketbroker",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class KeycloakSecurityService : SecurityService {

    override val userId: UUID?
        get() = otherClaims["sub"]?.let { UUID.fromString(it as String) }

    private val otherClaims: Map<String, Any>
        get() = principal?.claims ?: mapOf()

    private val principal: Jwt?
        @Suppress("UNCHECKED_CAST")
        get() = (authentication?.principal as Jwt?)

    override val roles: Set<String>
        get() {
            val any = otherClaims["realm_access"] as JSONObject?
            val map = any?.let { it["roles"] as JSONArray? }?.map { it as String }?.toSet()
            return map ?: setOf()
        }
    override val preferredUsername: String?
        get() = otherClaims["preferred_username"] as String?

    override val customerId: UUID?
        get() {
            val employeeId = otherClaims["customer_id"] as String?
            return employeeId?.let { UUID.fromString(it) }
        }

    override val clientId: String?
        get() = otherClaims["clientId"] as String?

    private val authentication: Authentication?
        get() {
            return when (SecurityContextHolder.getContext().authentication) {
                is AnonymousAuthenticationToken -> {
                    null
                }

                else -> (SecurityContextHolder.getContext().authentication)
            }
        }
}
