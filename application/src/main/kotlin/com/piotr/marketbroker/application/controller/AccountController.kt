package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.event.handler.AccountDetailsHandler
import com.piotr.marketbroker.application.event.handler.AccountSummaryHandler
import com.piotr.marketbroker.application.model.AccountSummaryResponseDTO
import com.piotr.marketbroker.application.model.OpeningOrderResponseDTO
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.extensions.Extension
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger(AccountController::class.toString())

@Tag(name="account")
@RestController
class AccountController(
    private val accountSummaryHandler: AccountSummaryHandler,
    private val accountDetailsHandler: AccountDetailsHandler
): AccountApi {

    @Operation(
        operationId = "getAccountSummary",
        summary = "getAccountSummary",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getAccountSummary")]),
            Extension(properties = [
                ExtensionProperty(name = "x-internal", value = "true")
            ])
        ]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Found Foos", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = AccountSummaryResponseDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [Content()]),
        ApiResponse(responseCode = "404", description = "Not found", content = [Content(
            examples = [ExampleObject(value = "{\"errorMessage\": \"AccountSummary not found.\"}")])])]
    )
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping(value = ["/account/summary"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getAccountSummary(): ResponseEntity<AccountSummaryResponseDTO> {
        log.info("getAccountSummary request")
        val accountSummary = accountSummaryHandler.getSummary()
        return if (accountSummary!=null) {
            ResponseEntity(accountSummary, HttpStatus.OK)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping(value = ["/account/opening-orders"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        operationId = "getOpeningOrders",
        summary = "getOpeningOrders",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getOpeningOrders")]),
            Extension(properties = [
                ExtensionProperty(name = "x-internal", value = "false")
            ])
        ]
    )
    override fun getOpeningOrders(): ResponseEntity<List<OpeningOrderResponseDTO>> {
        log.info("getPositions request")
        return ResponseEntity(accountDetailsHandler.getOpeningOrders(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping(value = ["/account/positions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        operationId = "getPositions",
        summary = "getPositions",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getPositions")]),
            Extension(properties = [
                ExtensionProperty(name = "x-internal", value = "false")
            ])
        ]
    )
    override fun getPositions(): ResponseEntity<List<PositionResponseDTO>> {
        log.info("getPositions request")
        return ResponseEntity(accountDetailsHandler.getPositions(), HttpStatus.OK)
    }

}
