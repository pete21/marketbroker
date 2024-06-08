package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.OrderRequestDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.application.service.OrdersService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import com.piotr.marketbroker.infrastructure.persistence.keys.KeysRepository
import com.piotr.marketbroker.infrastructure.persistence.order.Order
import io.swagger.v3.oas.annotations.tags.Tag
import com.piotr.marketbroker.common.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class OrdersController(
    private val keysRepository: KeysRepository,
    private val ordersService: OrdersService
): OrdersApi {

    private val log by logger()

    @Tag(name="orders")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/orders/{id}"]
    )
    override fun deleteOrderById(@PathVariable("id") id: Int): ResponseEntity<Unit> {
        log.info("deleteOrderId request: $id")
        val result = ordersService.deleteOrder(id)
        return if (result) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @Tag(name="orders")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/orders"],
        produces = ["application/json"]
    )
    override fun listOrders(): ResponseEntity<List<OrderResponseDTO>> {
        log.info("listOrders request")
        return ResponseEntity(ordersService.getOrders(), HttpStatus.OK)
    }

    @Tag(name="orders")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/orders/{id}"]
    )
    override fun getOrderById(@PathVariable("id") id: Int): ResponseEntity<OrderResponseDTO> {
        log.info("getOrderById request: $id")
        val result = ordersService.getOrder(id)
        return if (result!=null) {
            ResponseEntity(result, HttpStatus.OK)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @Tag(name="orders")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/orders/{id}"],
        produces = ["application/json"]
    )
    override fun amendOrderById(@PathVariable("id") id: Int): ResponseEntity<OrderResponseDTO> {
        TODO("Not yet implemented")
    }

    @Tag(name="orders")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/orders"],
        consumes = ["application/json"]
    )
    override fun createOrder(orderRequestDTO: OrderRequestDTO): ResponseEntity<OrderResponseDTO> {

        val lastTick = keysRepository.get(orderRequestDTO.quoteId)
        if (lastTick == null) {
            log.error("Order failed. No tick data for: " + orderRequestDTO.quoteId)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

        log.info("createOrder: $orderRequestDTO")
        val orderResponseDto: OrderResponseDTO?

        when (orderRequestDTO.orderMode) {
            0 -> {                                          //Market order, MO+SL, MO+TP, MO+SL+TP
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
                    0, //                    orderRequestDTO.orderMode,
                    orderRequestDTO.limitOrderPrice,
                    orderRequestDTO.stopOrderPrice,
                    orderRequestDTO.trailingPoint,
                    LocalDateTime.now(),
                    lastTick.key,
                    null,
                    null,
                    0
                )
                orderResponseDto = ordersService.requestTrade(order)
            }

            1 -> {                                          //Open order, OO+SL, OO+TP, OO+SL+TP
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    orderRequestDTO.price,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
                    1, //                    orderRequestDTO.orderMode,
                    orderRequestDTO.limitOrderPrice,
                    orderRequestDTO.stopOrderPrice,
                    0,
                    LocalDateTime.now(),
                    lastTick.key,
                    null,
                    null,
                    0
                )
                orderResponseDto = ordersService.insertOpenOrder(order)
            }

            2-> {                                          //Open order stop + SL + TP
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    orderRequestDTO.price,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
                    2, //           orderRequestDTO.orderMode,
                    orderRequestDTO.limitOrderPrice,
                    orderRequestDTO.stopOrderPrice,
                    0,
                    LocalDateTime.now(),
                    lastTick.key,
                    null,
                    null,
                    0
                )
                orderResponseDto = ordersService.insertOpenOrder(order)
            }

            4 -> {                                          //Close open position
                val order = Order(
                    0,
                    orderRequestDTO.marketId,
                    orderRequestDTO.quoteId,
                    if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                    orderRequestDTO.stake,
                    orderRequestDTO.direction,
                    4, //                    orderRequestDTO.orderMode,
                    0f,
                    0f,
                    0,
                    LocalDateTime.now(),
                    lastTick.key,
                    null,
                    null,
                    orderRequestDTO.positionId
                )
                orderResponseDto = ordersService.insertClosePosition(order)

            }

            else -> {
                log.error("Order failed. Bad orderMode: ${orderRequestDTO.orderMode}")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            }
        }
        return ResponseEntity(orderResponseDto, HttpStatus.OK)

    }
}
