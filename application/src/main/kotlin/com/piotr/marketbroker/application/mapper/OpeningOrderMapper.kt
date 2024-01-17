package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.openingorder.OpeningOrder

object OpeningOrderMapper {

    fun mapToOrderResponseDto(openingOrder: OpeningOrder) = OrderResponseDTO (

            orderId = openingOrder.orderID,
            currency = openingOrder.currency,
            currentPrice = openingOrder.currentPrice,
            direction = openingOrder.direction,
            expiryDate = openingOrder.expiryDate,
            goodTill = openingOrder.goodTill,
            iDOLimitOrderPrice = openingOrder.iDOLimitOrderPrice,
            iDOStopOrderPrice = openingOrder.iDOStopOrderPrice,
            iDOGuaranteed = openingOrder.iDOGuaranteed,
            isTriggered = openingOrder.isTriggered,
            limitOrderPrice = openingOrder.limitOrderPrice.toString(),
            margin = openingOrder.margin,
            marketID = openingOrder.marketID,
            marketTradable = openingOrder.marketTradable,
            period = openingOrder.period,
            creationTimeUTC= openingOrder.creationTimeUTC,
            quoteId = openingOrder.quoteId,
            quoteMode = openingOrder.quoteMode,
            stake = openingOrder.stake,
            status = openingOrder.status,
            stopOrderPrice = openingOrder.stopOrderPrice.toString(),
            type = openingOrder.type,
            trailingPoint = openingOrder.trailingPoint,
            isGuarantee = openingOrder.isGuarantee,
            isForceOpen = openingOrder.isForceOpen,
            orderPriceModeEnum = openingOrder.orderPriceModeEnum,
            currencySymbol = openingOrder.currencySymbol

    )
    
}
