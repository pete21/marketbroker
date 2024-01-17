package com.piotr.marketbroker.infrastructure.security

import java.util.UUID

interface SecurityService {

    val userId: UUID?

    val roles: Set<String>

    val preferredUsername: String?

    val customerId: UUID?

    val clientId: String?

    fun hasAnyRole(vararg roles: String) = roles.intersect(this.roles).isNotEmpty()

    fun hasRole(role: String) = roles.contains(role)

}
