package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.service.OrdersService
import io.swagger.v3.oas.annotations.tags.Tag
import com.piotr.marketbroker.domain.tick.port.TickState
import org.springframework.web.bind.annotation.RestController

@Tag(name="orderscopy")
@RestController
class OrdersCopyController(
    private val ordersService: OrdersService,
    private val tickState: TickState
) {

}
