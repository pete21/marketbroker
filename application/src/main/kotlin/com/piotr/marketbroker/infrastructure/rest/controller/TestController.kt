package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.extensions.Extension
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name="test")
@RestController
class TestController {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping("/hello")
    @Operation(
        operationId = "test",
        summary = "test",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "test")]),
            Extension(properties = [
                ExtensionProperty(name = "x-internal", value = "true")
            ])
        ]
    )
    fun test(): String {
        return "Hello from marketbroker."
    }

}
