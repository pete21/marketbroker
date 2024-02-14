package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.OrderRequestDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.application.service.OpeningOrdersService
import com.piotr.marketbroker.application.service.OrdersService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import com.piotr.marketbroker.infrastructure.persistence.keys.KeysRepository
import com.piotr.marketbroker.infrastructure.persistence.order.Order
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

private val log = KotlinLogging.logger(OrdersController::class.toString())

@RestController
class OrdersController(
    private val keysRepository: KeysRepository,
    private val ordersService: OrdersService,
    private val openingOrdersService: OpeningOrdersService
): OrdersApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/orders/{id}"]
    )
    override fun deleteOrderById(id: Int): ResponseEntity<Unit> {
        log.info("deleteOrdersId request")
        val result = ordersService.DeleteOrder(id)
        return if (result) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/orders"],
        produces = ["application/json"]
    )
    override fun listOrders(): ResponseEntity<List<OrderResponseDTO>> {
        log.info("listOrders request")
        return ResponseEntity(openingOrdersService.getOrders(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/orders/{id}"]
    )
    override fun getOrderById(id: Int): ResponseEntity<Unit> {
        log.info("deleteOrdersId request")
        val result = ordersService.GetOrder(id)
        return if (result) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/orders"],
        consumes = ["application/json"]
    )
    override fun createOrder(orderRequestDTO: OrderRequestDTO?): ResponseEntity<Unit> {

        val lastTick = keysRepository.get(orderRequestDTO!!.quoteId)
        if (lastTick == null) {
            log.error("Order failed. No tick data for: " + orderRequestDTO.quoteId)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

        log.info("createOrder: $orderRequestDTO")

        when (orderRequestDTO.orderMode) {
            0 -> {                                          //Market order, MO+SL, MO+TP, MO+SL+TP
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
//                    orderRequestDTO.orderMode,
                    orderRequestDTO.limitOrderPrice,
                    orderRequestDTO.stopOrderPrice,
                    orderRequestDTO.trailingPoint,
                    Instant.now().epochSecond,
                    lastTick.key,
                    null,
                    null,
                    0
                )
                ordersService.RequestTrade(order)
                return ResponseEntity.ok().build()

            }

            1 -> {                                          //Open order, OO+SL, OO+TP, OO+SL+TP
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    orderRequestDTO.price,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
//                    orderRequestDTO.orderMode,
                    orderRequestDTO.limitOrderPrice,
                    orderRequestDTO.stopOrderPrice,
                    0,
                    Instant.now().epochSecond,
                    lastTick.key,
                    null,
                    null,
                    0
                )
                ordersService.InsertOpenOrder(order)
                return ResponseEntity.ok().build()

            }

            2-> {}                                          //Open order stop + SL + TP

            4 -> {                                          //Close open order
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
//                    orderRequestDTO.orderMode,
                    0f,
                    0f,
                    0,
                    Instant.now().epochSecond,
                    lastTick.key,
                    null,
                    null,
                    orderRequestDTO.positionId
                )
                ordersService.InsertClosePosition(order)
                return ResponseEntity.ok().build()

            }

            else -> {
                log.error("Order failed. Bad orderMode: " + orderRequestDTO.orderMode)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            }
        }
        return ResponseEntity.ok().build()

    }
}
