package com.piotr.marketbroker.infrastructure.rest.controller.mapper

import com.piotr.marketbroker.application.model.OrderRequestDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.domain.order.OpenOrderResponse
import com.piotr.marketbroker.domain.order.Order
import com.piotr.marketbroker.domain.order.TradeRequestResponse
import com.piotr.marketbroker.domain.tick.Tick
import java.time.OffsetDateTime
import java.time.ZoneOffset

private const val ZONE_OFFSET_CET = "+01:00"

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
        openPrice = order.open_price,
        closePrice = order.close_price,
        openDate = order.open_date?.let { OffsetDateTime.of(order.open_date, ZoneOffset.of(ZONE_OFFSET_CET)) },
        closeDate = order.close_date?.let { OffsetDateTime.of(order.close_date, ZoneOffset.of(ZONE_OFFSET_CET)) },
        message = "",
        positionId = order.positionId,
        active = order.active,
        createdAt = OffsetDateTime.of(order.createdAt, ZoneOffset.of(ZONE_OFFSET_CET)),
        status = 0
    )

    fun mapTradeRequestToOrderResponseDTO(tradeRequestResponse: TradeRequestResponse) = OrderResponseDTO(
        orderId = tradeRequestResponse.orderId.toIntOrNull() ?: 0,
        marketId = tradeRequestResponse.marketId,
        quoteId = 0,
        price = tradeRequestResponse.price,
        stake = tradeRequestResponse.stake,
        direction = if (tradeRequestResponse.direction.lowercase() == "sell") -1 else 1,
        limitOrderPrice = tradeRequestResponse.limitOrderPrice.toFloatOrNull()?:0f,
        stopOrderPrice = tradeRequestResponse.stopOrderPrice.toFloatOrNull()?:0f,
        trailingPoint = false,
        message = tradeRequestResponse.message ?: "",
        positionId = tradeRequestResponse.positionId,
        active = true,
        status = tradeRequestResponse.status?:0,
        )

    fun mapOpenOrderResponseToOrderResponseDTO(openOrderResponse: OpenOrderResponse) = OrderResponseDTO(
        orderId = openOrderResponse.orderId.toInt(),
        marketId = if (openOrderResponse.marketId != null) openOrderResponse.marketId.toInt() else 0,
        quoteId = if (openOrderResponse.quoteId != null) openOrderResponse.quoteId.toInt() else 0,
        price = if (openOrderResponse.orderMode.lowercase()=="stop") openOrderResponse.stopOrderPrice.toFloat() else openOrderResponse.limitOrderPrice.toFloat(),
        stake = openOrderResponse.stake.toFloat(),
        direction = if (openOrderResponse.tradeMode.lowercase() == "sell") -1 else 1,
        limitOrderPrice = openOrderResponse.iDOLimitOrderPrice?.toFloat() ?: 0f,
        stopOrderPrice = openOrderResponse.iDOStopOrderPrice?.toFloat() ?: 0f,
        trailingPoint = openOrderResponse.iDOTrailingPoint == "1",
        message = openOrderResponse.message ?: "",
        positionId = 0,
        active = openOrderResponse.status==0,
        status = openOrderResponse.status
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

            5 -> Order.new(                                          //Close open position
                orderRequestDTO.marketId,
                orderRequestDTO.quoteId,
                if (orderRequestDTO.direction == -1) lastTick.bid else lastTick.ask,
                0f,
                orderRequestDTO.direction,
                orderRequestDTO.orderMode,
                0f,
                0f,
                false,
                lastTick.key,
                orderRequestDTO.positionId ?: error("Missing order id")
            )

            else -> null

        }

    }

}
