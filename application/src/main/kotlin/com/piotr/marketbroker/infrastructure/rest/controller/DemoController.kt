package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.DemoApi
import com.piotr.marketbroker.application.model.ResponseDTO
import com.piotr.marketbroker.application.model.SessionDTO
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import com.piotr.marketbroker.common.logger
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController(
    private val td365SessionService: TD365SessionService
): DemoApi {

    private val log by logger()

    @Tag(name="demo")
    @Operation(
        operationId = "demoSession",
        summary = "demoSession",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "demoSession")]),
            Extension(properties = [
                ExtensionProperty(name = "x-external", value = "true")
            ])
        ]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "demoSession", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = ResponseDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [Content()])
    ])
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun demoSession(sessionDTO: SessionDTO): ResponseEntity<ResponseDTO> {
        log.info("demoSession request: $sessionDTO")

        when (sessionDTO.state) {
            "START" -> {
                if (td365SessionService.demoSessionStart()) {
                        log.info("Session started")
                        return ResponseEntity<ResponseDTO>(ResponseDTO(0,"Session started"), HttpStatus.OK)
                    }
                log.warn("Session start error")
                return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Session start error"), HttpStatus.OK)
            }

            "STOP" -> {
                td365SessionService.sessionStop()
                log.info("Session stopped")
                return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Session stopped"), HttpStatus.OK)
            }
        }
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Unrecognized request"), HttpStatus.BAD_REQUEST)

    }

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
