package com.piotr.marketbroker.infrastructure.security

import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@ConditionalOnProperty(
    prefix = "com.piotr.security",
    name = ["enabled"],
    havingValue = "false",
    matchIfMissing = false
)
class FakeSecurityService : SecurityService {

    override val userId: UUID? = FAKE_UUID

    override val roles = setOf(SecurityRole.role_manager, SecurityRole.role_manager)

    override val preferredUsername: String? = "fake@piotr.com"

    override val customerId: UUID? = null

    override val clientId: String? = FAKE_CLIENT

    companion object {
        val FAKE_UUID: UUID = UUID(0,0)
        const val FAKE_CLIENT: String = "fake-client"
    }
}
