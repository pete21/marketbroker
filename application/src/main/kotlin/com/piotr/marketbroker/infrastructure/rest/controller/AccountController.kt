package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.AccountApi
import com.piotr.marketbroker.application.handler.AccountDetailsHandler
import com.piotr.marketbroker.domain.accountsummary.handler.AccountSummaryHandler
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
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.infrastructure.rest.controller.mapper.AccountDetailsMapper
import com.piotr.marketbroker.infrastructure.rest.controller.mapper.AccountSummaryMapper.mapToAccountSummaryResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Tag(name="account")
@RestController
class AccountController(
    private val accountSummaryHandler: AccountSummaryHandler,
    private val accountDetailsHandler: AccountDetailsHandler
): AccountApi {

    private val log by logger()

    @Operation(
        operationId = "getAccountSummary",
        summary = "getAccountSummary",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getAccountSummary")]),
            Extension(properties = [
                ExtensionProperty(name = "x-external", value = "true")
            ])
        ]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "AccountSummary", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = AccountSummaryResponseDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [Content()]),
        ApiResponse(responseCode = "404", description = "Not found", content = [Content(
            examples = [ExampleObject(value = "{\"message\": \"AccountSummary not found.\"}")])])]
    )
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getAccountSummary(): ResponseEntity<AccountSummaryResponseDTO> {
        log.info("getAccountSummary request")
        val accountSummary = accountSummaryHandler.getSummary()
        return accountSummary?.let { ResponseEntity(mapToAccountSummaryResponseDto(it),HttpStatus.OK) }
            ?: (ResponseEntity.notFound().build())

    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @Operation(
        operationId = "getOpeningOrders",
        summary = "getOpeningOrders",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getOpeningOrders")]),
            Extension(properties = [
                ExtensionProperty(name = "x-external", value = "true")
            ])
        ]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OpeningOrders", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = OpeningOrderResponseDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [Content()]),
        ApiResponse(responseCode = "404", description = "Not found", content = [Content(
            examples = [ExampleObject(value = "{\"message\": \"OpeningOrders not found.\"}")])])]
    )
    override fun getOpeningOrders(): ResponseEntity<List<OpeningOrderResponseDTO>> {
        log.info("getPositions request")
        val openingOrders = accountDetailsHandler.getOpeningOrders().map { AccountDetailsMapper.mapToOpeningOrderResponseDto(it) }
        return ResponseEntity(openingOrders, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @Operation(
        operationId = "getPositions",
        summary = "getPositions",
        description = "Operation Description",
        extensions = [
            Extension(name = "x-operation", properties = [ExtensionProperty(name = "name", value = "getPositions")]),
            Extension(properties = [
                ExtensionProperty(name = "x-external", value = "true")
            ])
        ]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Positions", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = PositionResponseDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [Content()]),
        ApiResponse(responseCode = "404", description = "Not found", content = [Content(
            examples = [ExampleObject(value = "{\"message\": \"Positions not found.\"}")])])]
    )
    override fun getPositions(): ResponseEntity<List<PositionResponseDTO>> {
        log.info("getPositions request")
        val positionRecords = accountDetailsHandler.getPositions().map { AccountDetailsMapper.mapToPositionResponseDto(it) }
        return ResponseEntity(positionRecords, HttpStatus.OK)
    }

}
