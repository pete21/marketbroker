package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.AccountResponseDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.application.service.AccountsService
import com.piotr.marketbroker.application.service.OpeningOrdersService
import com.piotr.marketbroker.application.service.PositionsService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping

private val log = KotlinLogging.logger {}

@Component
class AccountController(
    private val accountsService: AccountsService,
    private val positionsService: PositionsService,
    private val openingOrdersService: OpeningOrdersService
): AccountsApi, PositionsApi, OrdersApi {

    @GetMapping(value = ["/accounts"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getAccounts(): ResponseEntity<List<AccountResponseDTO>> {
        return ResponseEntity(accountsService.getAccounts(), HttpStatus.OK)
    }

    @GetMapping(value = ["/orders"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getOrders(): ResponseEntity<List<OrderResponseDTO>> {
        return ResponseEntity(openingOrdersService.getOrders(), HttpStatus.OK)
    }

    @GetMapping(value = ["/positions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getPositions(): ResponseEntity<List<PositionResponseDTO>> {
        return ResponseEntity(positionsService.getPositions(), HttpStatus.OK)
    }
}
