package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.AccountResponseDTO
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.application.service.AccountsService
import com.piotr.marketbroker.application.service.PositionsService
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
    private val accountsService: AccountsService,
    private val positionsService: PositionsService
): AccountsApi, PositionsApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping(value = ["/accounts"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getAccounts(): ResponseEntity<List<AccountResponseDTO>> {
        log.info("getAccounts request")
        return ResponseEntity(accountsService.getAccounts(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @GetMapping(value = ["/positions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getPositions(): ResponseEntity<List<PositionResponseDTO>> {
        log.info("getPositions request")
        return ResponseEntity(positionsService.getPosition(), HttpStatus.OK)
    }
}
