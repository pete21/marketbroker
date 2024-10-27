package com.piotr.marketbroker.infrastructure.rest.controller.mapper

import com.piotr.marketbroker.application.model.OrderRequestDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.domain.order.OpenOrderResponse
import com.piotr.marketbroker.domain.order.Order
import com.piotr.marketbroker.domain.order.TradeRequest
import com.piotr.marketbroker.domain.tick.Tick

object OrderMapper {

    fun mapOrderToOrderResponseDto(order: Order) = OrderResponseDTO(
        orderId = order.orderId,
        marketId = order.marketId,
        quoteId = order.quoteId,
        price = order.price,
        stake = order.stake,
        direction = order.direction,
        limitOrderPrice = order.limitOrderPrice,
        stopOrderPrice = order.stopOrderPrice,
        trailingPoint = order.trailingPoint,
        message = "",
        positionId = order.positionId,
        active = order.active
    )

    fun mapTradeRequestToOrderResponseDTO(tradeRequest: TradeRequest) = OrderResponseDTO(
        orderId = if (tradeRequest.orderId.isNotBlank()) { tradeRequest.orderId.toInt() } else 0,
        marketId = tradeRequest.marketId,
        quoteId = 0,
        price = tradeRequest.price,
        stake = tradeRequest.stake,
        direction = if (tradeRequest.direction == "sell") -1 else 1,
        limitOrderPrice = tradeRequest.limitOrderPrice.toFloat(),
        stopOrderPrice = tradeRequest.stopOrderPrice.toFloat(),
        trailingPoint = false,
        message = tradeRequest.message ?: "",
        positionId = tradeRequest.positionId,
        active = false
    )

    fun mapOpenOrderResponseToOrderResponseDTO(openOrderResponse: OpenOrderResponse) = OrderResponseDTO(
        orderId = openOrderResponse.orderId.toInt(),
        marketId = if (openOrderResponse.marketId != null) openOrderResponse.marketId.toInt() else 0,
        quoteId = if (openOrderResponse.quoteId != null) openOrderResponse.quoteId.toInt() else 0,
        price = if (openOrderResponse.orderMode=="Stop") openOrderResponse.stopOrderPrice.toFloat() else openOrderResponse.limitOrderPrice.toFloat(),
        stake = openOrderResponse.stake.toFloat().toInt(),
        direction = if (openOrderResponse.tradeMode == "Sell") -1 else 1,
        limitOrderPrice = openOrderResponse.iDOLimitOrderPrice?.toFloat() ?: 0f,
        stopOrderPrice = openOrderResponse.iDOStopOrderPrice?.toFloat() ?: 0f,
        trailingPoint = openOrderResponse.iDOTrailingPoint == "1",
        message = openOrderResponse.message ?: "",
        positionId = 0,
        active = openOrderResponse.status==0
    )

    fun mapOrderRequestDTOToOrder(orderRequestDTO: OrderRequestDTO, lastTick: Tick): Order? {

        return when (orderRequestDTO.orderMode) {
            0 -> Order.new(                                           //Market order, MO+SL, MO+TP, MO+SL+TP
                orderRequestDTO.marketId,
                orderRequestDTO.quoteId,
                if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                orderRequestDTO.stake,
                orderRequestDTO.direction,
                orderRequestDTO.orderMode,
                orderRequestDTO.limitOrderPrice,
                orderRequestDTO.stopOrderPrice,
                false,
                lastTick.key
            )

            1 -> Order.new(                                           //Open order, OO+SL, OO+TP, OO+SL+TP
                orderRequestDTO.marketId,
                orderRequestDTO.quoteId,
                orderRequestDTO.price,
                orderRequestDTO.stake,
                orderRequestDTO.direction,
                orderRequestDTO.orderMode,
                orderRequestDTO.limitOrderPrice,
                orderRequestDTO.stopOrderPrice,
                orderRequestDTO.trailingPoint,
                lastTick.key
            )

            2 -> Order.new(                                          //Open order stop + SL + TP
                orderRequestDTO.marketId,
                orderRequestDTO.quoteId,
                orderRequestDTO.price,
                orderRequestDTO.stake,
                orderRequestDTO.direction,
                orderRequestDTO.orderMode,
                orderRequestDTO.limitOrderPrice,
                orderRequestDTO.stopOrderPrice,
                orderRequestDTO.trailingPoint,
                lastTick.key
            )

            4 -> Order.new(                                          //Close open position
                orderRequestDTO.marketId,
                orderRequestDTO.quoteId,
                if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                orderRequestDTO.stake,
                orderRequestDTO.direction,
                orderRequestDTO.orderMode,
                0f,
                0f,
                false,
                lastTick.key,
                orderRequestDTO.positionId ?: error("Missing position id")
            )

            else -> null

        }

    }

}
