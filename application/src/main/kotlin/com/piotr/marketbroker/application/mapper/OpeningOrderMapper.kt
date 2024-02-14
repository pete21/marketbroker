package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.accountdetailsopeningorder.AccountDetailsOpeningOrder

object OpeningOrderMapper {

    fun mapToOrderResponseDto(accountDetailsOpeningOrder: AccountDetailsOpeningOrder) = OrderResponseDTO (

            orderId = accountDetailsOpeningOrder.orderID,
            currency = accountDetailsOpeningOrder.currency,
            currentPrice = accountDetailsOpeningOrder.currentPrice,
            direction = accountDetailsOpeningOrder.direction,
            expiryDate = accountDetailsOpeningOrder.expiryDate,
            goodTill = accountDetailsOpeningOrder.goodTill,
            iDOLimitOrderPrice = accountDetailsOpeningOrder.iDOLimitOrderPrice,
            iDOStopOrderPrice = accountDetailsOpeningOrder.iDOStopOrderPrice,
            iDOGuaranteed = accountDetailsOpeningOrder.iDOGuaranteed,
            isTriggered = accountDetailsOpeningOrder.isTriggered,
            limitOrderPrice = accountDetailsOpeningOrder.limitOrderPrice.toString(),
            margin = accountDetailsOpeningOrder.margin,
            marketID = accountDetailsOpeningOrder.marketID,
            marketTradable = accountDetailsOpeningOrder.marketTradable,
            period = accountDetailsOpeningOrder.period,
            creationTimeUTC= accountDetailsOpeningOrder.creationTimeUTC,
            quoteId = accountDetailsOpeningOrder.quoteId,
            quoteMode = accountDetailsOpeningOrder.quoteMode,
            stake = accountDetailsOpeningOrder.stake,
            status = accountDetailsOpeningOrder.status,
            stopOrderPrice = accountDetailsOpeningOrder.stopOrderPrice.toString(),
            type = accountDetailsOpeningOrder.type,
            trailingPoint = accountDetailsOpeningOrder.trailingPoint,
            isGuarantee = accountDetailsOpeningOrder.isGuarantee,
            isForceOpen = accountDetailsOpeningOrder.isForceOpen,
            orderPriceModeEnum = accountDetailsOpeningOrder.orderPriceModeEnum,
            currencySymbol = accountDetailsOpeningOrder.currencySymbol

    )
    
}
