package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.event.handler.AccountDetailsHandler
import com.piotr.marketbroker.application.event.handler.AccountSummaryHandler
import com.piotr.marketbroker.application.model.AccountSummaryResponseDTO
import com.piotr.marketbroker.application.model.OpeningOrderResponseDTO
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger(AccountController::class.toString())

@RestController
class AccountController(
    private val accountSummaryHandler: AccountSummaryHandler,
    private val accountDetailsHandler: AccountDetailsHandler
): AccountApi {

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
    override fun getOpeningOrders(): ResponseEntity<List<OpeningOrderResponseDTO>> {
        log.info("getPositions request")
        return ResponseEntity(accountDetailsHandler.getOpeningOrders(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping(value = ["/account/positions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getPositions(): ResponseEntity<List<PositionResponseDTO>> {
        log.info("getPositions request")
        return ResponseEntity(accountDetailsHandler.getPositions(), HttpStatus.OK)
    }

}
