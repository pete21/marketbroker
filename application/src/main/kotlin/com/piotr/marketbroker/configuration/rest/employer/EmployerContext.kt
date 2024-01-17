package com.piotr.marketbroker.configuration.rest.employer

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import java.util.UUID

@Component
@RequestScope
class EmployerContext {
    var _employerId: UUID? = null

    var employerId: UUID
        get() {
            return _employerId ?: throw NoEmployerIdProvidedException()
        }
        set(value) {
            _employerId = value
        }
}
