package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.order.Order
import java.time.ZoneOffset

object OrderMapper {

    fun mapOrderToOrderResponseDto(order: Order) = OrderResponseDTO (
        id = order.id,
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
}