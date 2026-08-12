package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.OrdersApi
import com.piotr.marketbroker.application.model.OrderRequestDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.application.service.OrdersService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.tags.Tag
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.tick.port.TickState
import com.piotr.marketbroker.infrastructure.rest.controller.mapper.OrderMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name="orders")
@RestController
class OrdersController(
    private val ordersService: OrdersService,
    private val tickState: TickState
): OrdersApi {

    private val log by logger()

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun deleteOrderById(@PathVariable("id") id: Int): ResponseEntity<Unit> {
        log.info("deleteOrderId request: $id")
        return if (ordersService.deleteOrder(id)) ResponseEntity.ok().build()
        else ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun listOrders(): ResponseEntity<List<OrderResponseDTO>> {
        log.info("listOrders request")
        return ResponseEntity(
            ordersService.getOrders().map { OrderMapper.mapOrderToOrderResponseDto(it) },
            HttpStatus.OK
        )
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun listOrdersHistory(): ResponseEntity<List<OrderResponseDTO>> {
        log.info("listOrdersHistory request")
        return ResponseEntity(
            ordersService.getHistory()?.map { OrderMapper.mapHistoryToOrderResponseDto(it) },
            HttpStatus.OK
        )
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getOrderById(@PathVariable("id") id: Int): ResponseEntity<OrderResponseDTO> {
        log.info("getOrderById request: $id")
        return ordersService.getOrder(id)
            ?.let { ResponseEntity(OrderMapper.mapOpenOrderResponseToOrderResponseDTO(it), HttpStatus.OK) }
            ?: ResponseEntity.notFound().build()
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun amendOrderById(@PathVariable("id") id: Int): ResponseEntity<OrderResponseDTO> {
        TODO("Not yet implemented")
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun createOrder(orderRequestDTO: OrderRequestDTO): ResponseEntity<OrderResponseDTO> {

        val lastTick = tickState.get(orderRequestDTO.quoteId)
        if (lastTick == null) {
            log.error("Order failed. No tick data for: " + orderRequestDTO.quoteId)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

        log.info("createOrder: $orderRequestDTO")
        val orderResponseDto: OrderResponseDTO?

        val order = OrderMapper.mapOrderRequestDTOToOrder(orderRequestDTO, lastTick)                //orderMode=5 requires order update
        when (order?.orderModeID) {
            0 -> {                                         //Market order, +SL, +TP, +SL+TP
                val orderResponseBody = ordersService.requestTrade(order)
                log.info("requestTrade response: $orderResponseBody")
                orderResponseDto = orderResponseBody.let { OrderMapper.mapTradeRequestToOrderResponseDTO(it) }
            }
            1 -> {                                         //Open order, OO+SL, OO+TP, OO+SL+TP
                val orderResponseBody = ordersService.insertOpenOrder(order)
                log.info("insertOpenOrder response: $orderResponseBody")
                orderResponseDto = orderResponseBody.let { OrderMapper.mapOpenOrderResponseToOrderResponseDTO(it) }
            }
            2 ->                                          //Open order stop + SL + TP
                orderResponseDto = ordersService.insertOpenOrder(order).let { OrderMapper.mapOpenOrderResponseToOrderResponseDTO(it) }
            4 ->                                          //Close open position by positionId
                orderResponseDto = ordersService.insertClosePosition(order).let { OrderMapper.mapTradeRequestToOrderResponseDTO(it) }
            5 ->                                          //Full close of open position by orderId
                orderResponseDto = ordersService.insertClosePosition(order).let { OrderMapper.mapTradeRequestToOrderResponseDTO(it) }
            else -> {
                log.error("Order failed. Bad orderMode: ${orderRequestDTO.orderMode}")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            }
        }
        return ResponseEntity(orderResponseDto, if (orderResponseDto.status==0) HttpStatus.OK else HttpStatus.BAD_REQUEST)

    }
}
