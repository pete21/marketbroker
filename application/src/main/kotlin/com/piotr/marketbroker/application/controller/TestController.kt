package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("FunctionOnlyReturningConstant")
class TestController {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping("/hello")
    fun test(): String {
        return "Hello from marketbroker."
    }
}
