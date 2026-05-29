package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.service.TD365SessionService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.extensions.Extension
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
//import com.piotr.marketbroker.common.logger
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigController(
    private val td365SessionService: TD365SessionService
) {

//    private val log by logger()


    @Tag(name="config")
    @Operation(
        operationId = "getConfig",
        summary = "getConfig",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getConfig")]),
            Extension(properties = [
                ExtensionProperty(name = "x-internal", value = "true")
            ])
        ]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "getConfig", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = String::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [Content()])
    ])
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/config"],
        produces = ["application/json"]
    )
    fun getConfig() : String {
        return td365SessionService.getTD365ConfigurationProperties()
    }

}
