package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.order.OpenOrderResponse
import com.piotr.marketbroker.infrastructure.persistence.order.Order
import com.piotr.marketbroker.infrastructure.persistence.order.TradeRequest
import java.time.OffsetDateTime
import java.time.ZoneOffset

object OrderMapper {

    fun mapOrderToOrderResponseDto(order: Order) = OrderResponseDTO (
        orderId = 0,
        marketId = order.marketId,
        quoteId = order.quoteId,
        price = order.price,
        stake = order.stake,
        direction = order.direction,
        limitOrderPrice = order.limitOrderPrice,
        stopOrderPrice = order.stopOrderPrice,
        trailingPoint = order.trailingPoint,
        createdAt = order.createdAt.atOffset(ZoneOffset.UTC),
        positionId = order.positionId
    )

    fun mapTradeRequestToOrderResponseDTO(tradeRequest: TradeRequest) = OrderResponseDTO (
        orderId = tradeRequest.orderId.toInt(),
        marketId = tradeRequest.marketId,
        quoteId = 0,
        price = tradeRequest.price,
        stake = tradeRequest.stake,
        direction = tradeRequest.direction.toInt(),
        limitOrderPrice = tradeRequest.limitOrderPrice.toFloat(),
        stopOrderPrice = tradeRequest.stopOrderPrice.toFloat(),
        trailingPoint = 0,
        createdAt = OffsetDateTime.MIN,
        positionId = tradeRequest.positionId
    )

    fun mapOpenOrderResponseToOrderResponseDTO(openOrderResponse: OpenOrderResponse) = OrderResponseDTO (
        orderId = openOrderResponse.orderId.toInt(),
        marketId = if (openOrderResponse.marketId!=null) openOrderResponse.marketId.toInt() else 0,
        quoteId = if (openOrderResponse.quoteId!=null) openOrderResponse.quoteId.toInt() else 0,
        price = openOrderResponse.limitOrderPrice.toFloat(),
        stake = openOrderResponse.stake.toFloat().toInt(),
        direction = if (openOrderResponse.tradeMode=="Sell") -1 else 1,
        limitOrderPrice = openOrderResponse.iDOLimitOrderPrice?.toFloat() ?: 0f,
        stopOrderPrice = openOrderResponse.iDOStopOrderPrice?.toFloat() ?: 0f,
        trailingPoint = openOrderResponse.iDOTrailingPoint?.toInt() ?: 0,
        createdAt = OffsetDateTime.MIN,
        positionId = 0
    )
}